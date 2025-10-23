package dev.loki.alarm.usecase

import dev.loki.alarm.model.Alarm
import dev.loki.alarm.repository.AlarmRepository

class GetAlarmByIdUseCase(
    private val repository: AlarmRepository
) {

    suspend operator fun invoke(id: Long): Alarm? {
        return repository.getAlarmById(id)
    }
}