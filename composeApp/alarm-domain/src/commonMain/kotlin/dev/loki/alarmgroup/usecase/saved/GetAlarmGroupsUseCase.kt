package dev.loki.alarmgroup.usecase.saved

import dev.loki.DomainResult
import dev.loki.alarmgroup.model.AlarmGroup
import dev.loki.alarmgroup.model.AlarmMainSort
import dev.loki.alarmgroup.repository.AlarmGroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetAlarmGroupsUseCase(
    private val repository: AlarmGroupRepository
) {

    operator fun invoke(sort: AlarmMainSort): Flow<DomainResult<List<AlarmGroup>>> {
        return repository.getAlarmGroups(sort)
            .map<List<AlarmGroup>, DomainResult<List<AlarmGroup>>> { groups ->
                DomainResult.Success(groups)
            }
            .catch { e ->
                emit(DomainResult.Error(e))
            }
    }
}