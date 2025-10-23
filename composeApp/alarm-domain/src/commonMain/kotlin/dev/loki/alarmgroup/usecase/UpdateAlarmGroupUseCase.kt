package dev.loki.alarmgroup.usecase

import dev.loki.AlarmScheduler
import dev.loki.alarmgroup.model.AlarmGroup
import dev.loki.alarmgroup.repository.AlarmGroupRepository
import kotlinx.coroutines.flow.first

class UpdateAlarmGroupUseCase(
    private val repository: AlarmGroupRepository,
    private val alarmScheduler: AlarmScheduler,
) {

    suspend operator fun invoke(alarmGroup: AlarmGroup) {
        repository.updateAlarmGroup(alarmGroup)
        val (group, alarms) = repository.getAlarmGroupWithAlarms(alarmGroup.id).first()

        if (!alarmGroup.isActivated) {
            alarms.forEach { alarm ->
                alarmScheduler.cancel(group.repeatDays, alarm)
            }
        } else {
            alarms.filter { it.isActivated }.forEach { alarm ->
                alarmScheduler.schedule(group.repeatDays, alarm)
            }
        }
    }
}