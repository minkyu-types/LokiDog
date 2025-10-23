package dev.loki.alarmgroup.usecase

import dev.loki.alarmgroup.model.AlarmGroup
import dev.loki.alarmgroup.repository.AlarmGroupRepository

class GetAlarmGroupById(
    private val repository: AlarmGroupRepository
) {

    suspend operator fun invoke(id: Long): AlarmGroup? {
        return repository.getAlarmGroupById(id)
    }
}