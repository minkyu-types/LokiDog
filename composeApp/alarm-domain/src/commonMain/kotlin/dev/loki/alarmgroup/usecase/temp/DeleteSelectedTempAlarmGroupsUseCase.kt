package dev.loki.alarmgroup.usecase.temp

import dev.loki.alarmgroup.model.TempAlarmGroup
import dev.loki.alarmgroup.repository.AlarmGroupRepository

class DeleteSelectedTempAlarmGroupsUseCase(
    private val repository: AlarmGroupRepository
) {

    suspend operator fun invoke(tempAlarmGroups: List<TempAlarmGroup>) {
        repository.deleteSelectedTempAlarmGroups(tempAlarmGroups)
    }
}