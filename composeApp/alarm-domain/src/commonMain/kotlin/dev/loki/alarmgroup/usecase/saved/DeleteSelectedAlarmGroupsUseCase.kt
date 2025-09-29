package dev.loki.alarmgroup.usecase.saved

import dev.loki.alarmgroup.repository.AlarmGroupRepository
import dev.loki.alarmgroup.model.AlarmGroup

class DeleteSelectedAlarmGroupsUseCase(
    private val repository: AlarmGroupRepository
) {

    suspend operator fun invoke(alarmGroups: List<AlarmGroup>) {
        repository.deleteSelectedAlarmGroups(alarmGroups)
    }
}