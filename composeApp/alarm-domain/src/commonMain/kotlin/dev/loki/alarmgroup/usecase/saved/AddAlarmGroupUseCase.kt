package dev.loki.alarmgroup.usecase.saved

import dev.loki.alarmgroup.repository.AlarmGroupRepository
import dev.loki.alarmgroup.model.AlarmGroup

class AddAlarmGroupUseCase(
    private val repository: AlarmGroupRepository
) {

    suspend operator fun invoke(alarmGroup: AlarmGroup) {
        repository.createAlarmGroup(alarmGroup)
    }
}