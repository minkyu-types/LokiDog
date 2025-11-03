package dev.loki.alarm.usecase

import dev.loki.alarm.model.Alarm
import dev.loki.alarm.repository.AlarmRepository

class UpsertAlarmUseCase(
    private val repository: AlarmRepository,
) {

    suspend operator fun invoke(alarm: Alarm) {
        repository.upsertAlarm(alarm)
    }
}