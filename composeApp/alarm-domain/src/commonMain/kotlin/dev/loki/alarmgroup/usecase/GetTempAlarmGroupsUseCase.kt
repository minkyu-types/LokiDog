package dev.loki.alarmgroup.usecase

import dev.loki.DomainResult
import dev.loki.alarmgroup.model.AlarmGroup
import dev.loki.alarmgroup.repository.AlarmGroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetTempAlarmGroupsUseCase(
    private val repository: AlarmGroupRepository
) {

    operator fun invoke(): Flow<DomainResult<List<AlarmGroup>>> {
        return repository.getTempAlarmGroups()
            .map<List<AlarmGroup>, DomainResult<List<AlarmGroup>>> { groups ->
                DomainResult.Success(groups)
            }
            .catch { e ->
                emit(DomainResult.Error(e))
            }
    }
}