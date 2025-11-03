package dev.loki.alarm.usecase

import dev.loki.AlarmScheduler
import dev.loki.alarm.model.Alarm
import dev.loki.alarm.repository.AlarmRepository

class DeleteAlarmUseCase(
    private val repository: AlarmRepository,
    private val alarmScheduler: AlarmScheduler
) {

    suspend operator fun invoke(alarm: Alarm) {
        repository.deleteAlarm(alarm)
        alarmScheduler.cancel(emptySet(),  alarm)
    }
}