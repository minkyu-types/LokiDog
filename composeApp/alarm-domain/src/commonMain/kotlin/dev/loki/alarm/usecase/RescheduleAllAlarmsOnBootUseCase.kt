package dev.loki.alarm.usecase

import co.touchlab.kermit.Logger
import dev.loki.AlarmScheduler
import dev.loki.alarmgroup.model.AlarmMainSort
import dev.loki.alarmgroup.repository.AlarmGroupRepository
import kotlinx.coroutines.flow.first

class RescheduleAllAlarmsOnBootUseCase(
    private val alarmGroupRepository: AlarmGroupRepository,
    private val alarmScheduler: AlarmScheduler
) {

    suspend operator fun invoke() {
        try {
            val alarmGroups = alarmGroupRepository.getAlarmGroups(AlarmMainSort.MOST_RECENT_CREATED).first()
            val activeGroups = alarmGroups.filter { it.isActivated }

            Logger.d { "RescheduleAllAlarmsOnBoot: Found ${activeGroups.size} active groups" }

            activeGroups.forEach { group ->
                val groupWithAlarms = alarmGroupRepository.getAlarmGroupWithAlarms(group.id).first()

                groupWithAlarms.alarms
                    .filter { it.isActivated }
                    .forEach { alarm ->
                        alarmScheduler.schedule(group.repeatDays, alarm)
                        Logger.d { "Rescheduled alarm: ${alarm.time} for group ${group.id}, days: ${group.repeatDays}" }
                    }
            }

            Logger.i { "RescheduleAllAlarmsOnBoot: Successfully rescheduled all active alarms" }
        } catch (e: Exception) {
            Logger.e(e) { "RescheduleAllAlarmsOnBoot: Failed to reschedule alarms" }
        }
    }
}