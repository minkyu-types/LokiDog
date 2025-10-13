package dev.loki.alarm.usecase

import dev.loki.alarm.model.Alarm
import dev.loki.alarm.repository.AlarmRepository

class AddAlarmUseCase(
    private val repository: AlarmRepository
) {

    suspend operator fun invoke(alarm: Alarm) {
        repository.createAlarm(alarm)
    }
}