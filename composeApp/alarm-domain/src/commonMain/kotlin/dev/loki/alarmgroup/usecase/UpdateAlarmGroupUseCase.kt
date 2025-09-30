package dev.loki.alarmgroup.usecase

import dev.loki.alarmgroup.model.AlarmGroup
import dev.loki.alarmgroup.repository.AlarmGroupRepository

class UpdateAlarmGroupUseCase(
    private val repository: AlarmGroupRepository
) {

    suspend operator fun invoke(alarmGroup: AlarmGroup) {
        repository.updateAlarmGroup(alarmGroup)
    }
}