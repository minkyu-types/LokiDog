package dev.loki.alarmgroup.usecase.temp

import dev.loki.DomainResult
import dev.loki.alarmgroup.model.TempAlarmGroup
import dev.loki.alarmgroup.repository.AlarmGroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetTempAlarmGroupsUseCase(
    private val repository: AlarmGroupRepository
) {

    operator fun invoke(): Flow<DomainResult<List<TempAlarmGroup>>> {
        return repository.getTempAlarmGroups()
            .map<List<TempAlarmGroup>, DomainResult<List<TempAlarmGroup>>> { groups ->
                DomainResult.Success(groups)
            }
            .catch { e ->
                emit(DomainResult.Error(e))
            }
    }
}