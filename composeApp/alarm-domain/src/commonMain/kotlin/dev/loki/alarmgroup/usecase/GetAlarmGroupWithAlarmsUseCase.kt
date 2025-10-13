package dev.loki.alarmgroup.usecase

import dev.loki.DomainResult
import dev.loki.alarmgroup.model.AlarmGroupWithAlarms
import dev.loki.alarmgroup.repository.AlarmGroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetAlarmGroupWithAlarmsUseCase(
    private val repository: AlarmGroupRepository
) {

    operator fun invoke(id: Long): Flow<DomainResult<AlarmGroupWithAlarms>> {
        return repository.getAlarmGroupWithAlarms(id)
            .map<AlarmGroupWithAlarms, DomainResult<AlarmGroupWithAlarms>> { groups ->
                DomainResult.Success(groups)
            }
            .catch { e ->
                DomainResult.Error(e)
            }
    }
}