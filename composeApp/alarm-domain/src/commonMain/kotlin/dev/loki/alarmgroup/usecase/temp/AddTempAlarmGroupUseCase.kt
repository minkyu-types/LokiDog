package dev.loki.alarmgroup.usecase.temp

import dev.loki.alarmgroup.repository.AlarmGroupRepository
import dev.loki.alarmgroup.model.TempAlarmGroup

class AddTempAlarmGroupUseCase(
    private val repository: AlarmGroupRepository
) {

    suspend operator fun invoke(tempAlarmGroup: TempAlarmGroup) {
        repository.createTempAlarmGroup(tempAlarmGroup)
    }
}