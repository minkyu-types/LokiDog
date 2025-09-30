package dev.loki.dog.feature.temp

import dev.loki.DomainResult
import dev.loki.alarmgroup.usecase.DeleteAlarmGroupUseCase
import dev.loki.alarmgroup.usecase.GetTempAlarmGroupsUseCase
import dev.loki.alarmgroup.usecase.UpdateAlarmGroupUseCase
import dev.loki.dog.feature.base.BaseAction
import dev.loki.dog.feature.base.BaseStore
import dev.loki.dog.feature.base.LoadState
import dev.loki.dog.mapper.AlarmGroupMapper
import dev.loki.dog.model.AlarmGroupModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.scope.Scope

class TempAlarmGroupsStore(
    private val coroutineScope: CoroutineScope,
    scope: Scope
) : BaseStore<TempAlarmGroupsState, TempAlarmGroupsSideEffect>(
    coroutineScope = coroutineScope,
    scope = scope,
    initialState = TempAlarmGroupsState()
) {
    private val getTempAlarmGroupsUseCase: GetTempAlarmGroupsUseCase by inject()
    private val updateAlarmGroupUseCase: UpdateAlarmGroupUseCase by inject()
    private val deleteAlarmGroupUseCase: DeleteAlarmGroupUseCase by inject()
    private val alarmGroupMapper: AlarmGroupMapper by inject()

    init {
        coroutineScope.launch {
            getTempAlarmGroupsUseCase().collect { domainResult ->
                when (domainResult) {
                    is DomainResult.Success -> {
                        val tempAlarmGroups = domainResult.data.map { domainTempGroup ->
                            alarmGroupMapper.mapToPresentation(domainTempGroup)
                        }
                        setState { copy(tempAlarmGroupList = tempAlarmGroups) }
                    }

                    is DomainResult.Error -> {
                        setState {
                            copy(
                                loadState = LoadState.Error(
                                    domainResult.throwable.message ?: "오류 발생"
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun dispatch(action: BaseAction) {
        when (action) {
            is TempAlarmGroupsAction.Save -> {
                coroutineScope.launch {
                    saveTempAlarmGroup(action.alarmGroup)
                }
            }

            is TempAlarmGroupsAction.Update -> {
                coroutineScope.launch {
                    updateTempAlarmGroup(action.alarmGroup)
                }
            }

            is TempAlarmGroupsAction.Delete -> {
                coroutineScope.launch {
                    deleteTempAlarmGroup(action.alarmGroup)
                }
            }
        }
    }

    private suspend fun saveTempAlarmGroup(alarmGroup: AlarmGroupModel) {
        val data = alarmGroup.copy(
            isTemp = false
        )
        val domainData = alarmGroupMapper.mapToDomain(data)
        updateAlarmGroupUseCase(domainData)
    }

    private suspend fun updateTempAlarmGroup(alarmGroup: AlarmGroupModel) {
        val domainData = alarmGroupMapper.mapToDomain(alarmGroup)
        updateAlarmGroupUseCase(domainData)
    }

    private suspend fun deleteTempAlarmGroup(alarmGroup: AlarmGroupModel) {
        val domainData = alarmGroupMapper.mapToDomain(alarmGroup)
        deleteAlarmGroupUseCase(domainData)
    }
}