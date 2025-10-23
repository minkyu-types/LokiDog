package dev.loki.alarm.usecase

import dev.loki.AlarmScheduler
import dev.loki.alarm.model.Alarm
import dev.loki.alarm.repository.AlarmRepository
import kotlinx.datetime.DayOfWeek

class UpsertAlarmUseCase(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler
) {

    suspend operator fun invoke(repeatDays: Set<DayOfWeek>, alarm: Alarm) {
        repository.upsertAlarm(alarm)
    }
}