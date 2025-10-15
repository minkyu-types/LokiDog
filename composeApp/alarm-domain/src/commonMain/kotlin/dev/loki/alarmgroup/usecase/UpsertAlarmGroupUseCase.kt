package dev.loki.alarmgroup.usecase

import dev.loki.alarmgroup.repository.AlarmGroupRepository
import dev.loki.alarmgroup.model.AlarmGroup

class UpsertAlarmGroupUseCase(
    private val repository: AlarmGroupRepository
) {

    suspend operator fun invoke(alarmGroup: AlarmGroup): Long {
        return repository.upsertAlarmGroup(alarmGroup)
    }
}