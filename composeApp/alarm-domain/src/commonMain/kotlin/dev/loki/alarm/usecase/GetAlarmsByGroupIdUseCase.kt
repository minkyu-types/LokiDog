package dev.loki.alarm.usecase

import dev.loki.alarm.model.Alarm
import dev.loki.alarm.repository.AlarmRepository

class GetAlarmsByGroupIdUseCase(
    private val repository: AlarmRepository
) {

    suspend operator fun invoke(groupId: Long): List<Alarm> {
        return repository.getAlarmsByGroupId(groupId)
    }
}