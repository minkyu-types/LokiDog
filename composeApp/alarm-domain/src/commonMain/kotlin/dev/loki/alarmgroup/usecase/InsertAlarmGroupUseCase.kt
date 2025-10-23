package dev.loki.alarmgroup.usecase

import dev.loki.AlarmScheduler
import dev.loki.alarmgroup.repository.AlarmGroupRepository
import dev.loki.alarmgroup.model.AlarmGroup
import kotlinx.coroutines.flow.first

class InsertAlarmGroupUseCase(
    private val repository: AlarmGroupRepository,
    private val alarmScheduler: AlarmScheduler
) {

    suspend operator fun invoke(alarmGroup: AlarmGroup): Long {
        val groupId = repository.createAlarmGroup(alarmGroup)
        val (group, alarms) = repository.getAlarmGroupWithAlarms(groupId).first()

        if (!alarmGroup.isTemp) {
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

        return groupId
    }
}