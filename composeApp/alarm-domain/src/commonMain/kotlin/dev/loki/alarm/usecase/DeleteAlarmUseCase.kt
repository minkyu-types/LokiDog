package dev.loki.alarm.usecase

import dev.loki.alarm.model.Alarm
import dev.loki.alarm.repository.AlarmRepository

class DeleteAlarmUseCase(
    private val repository: AlarmRepository
) {

    suspend operator fun invoke(alarm: Alarm) {
        repository.deleteAlarm(alarm)
    }
}