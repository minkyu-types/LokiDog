package dev.loki.alarm.usecase

import co.touchlab.kermit.Logger
import dev.loki.AlarmScheduler
import dev.loki.alarm.model.Alarm
import kotlinx.datetime.DayOfWeek

class RescheduleAlarmUseCase(
    private val alarmScheduler: AlarmScheduler
) {

    suspend operator fun invoke(isActivated: Boolean, repeatDays: Set<DayOfWeek>, alarm: Alarm) {
        if (isActivated && alarm.isActivated) {
            require(repeatDays.isNotEmpty())
            alarmScheduler.schedule(repeatDays, alarm)
            Logger.e { "qqqq 스케줄링 : ${alarm.time}, $repeatDays" }
        } else {
            alarmScheduler.cancel(repeatDays, alarm)
        }
    }
}