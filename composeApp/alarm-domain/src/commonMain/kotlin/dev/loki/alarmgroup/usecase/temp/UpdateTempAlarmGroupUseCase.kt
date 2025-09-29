package dev.loki.alarmgroup.usecase.temp

import dev.loki.alarmgroup.model.TempAlarmGroup
import dev.loki.alarmgroup.repository.AlarmGroupRepository

class UpdateTempAlarmGroupUseCase(
    private val repository: AlarmGroupRepository
) {

    suspend operator fun invoke(tempAlarmGroup: TempAlarmGroup) {
        repository.updateTempAlarmGroup(tempAlarmGroup)
    }
}