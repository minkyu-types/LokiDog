package dev.loki.dog.feature.main

import dev.loki.DomainResult
import dev.loki.alarm.usecase.AddAlarmUseCase
import dev.loki.alarmgroup.model.AlarmMainSort
import dev.loki.alarmgroup.usecase.AddAlarmGroupUseCase
import dev.loki.alarmgroup.usecase.DeleteAlarmGroupUseCase
import dev.loki.alarmgroup.usecase.DeleteSelectedAlarmGroupsUseCase
import dev.loki.alarmgroup.usecase.GetAlarmGroupsUseCase
import dev.loki.alarmgroup.usecase.UpdateAlarmGroupUseCase
import dev.loki.dog.feature.base.BaseAction
import dev.loki.dog.feature.base.BaseStore
import dev.loki.dog.feature.base.LoadState
import dev.loki.dog.mapper.AlarmGroupMapper
import dev.loki.dog.model.AlarmGroupModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.scope.Scope

class AlarmMainStore(
    coroutineScope: CoroutineScope,
    scope: Scope
) : BaseStore<AlarmMainState, AlarmMainSideEffect>(
    coroutineScope = coroutineScope,
    scope = scope,
    initialState = AlarmMainState()
) {
    private val getAlarmGroupsUseCase: GetAlarmGroupsUseCase by inject()
    private val addAlarmUseCase: AddAlarmUseCase by inject()
    private val addAlarmGroupUseCase: AddAlarmGroupUseCase by inject()
    private val updateAlarmGroupUseCase: UpdateAlarmGroupUseCase by inject()
    private val deleteAlarmGroupUseCase: DeleteAlarmGroupUseCase by inject()
    private val deleteSelectedAlarmGroupUseCase: DeleteSelectedAlarmGroupsUseCase by inject()
    private val alarmGroupMapper: AlarmGroupMapper by inject()

    private val currentSort = MutableStateFlow(AlarmMainSort.MOST_RECENT_CREATED)

    init {
        observeAlarmGroups()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeAlarmGroups() {
        viewModelScope.launch {
            currentSort.flatMapLatest { sort ->
                getAlarmGroupsUseCase(sort)
            }.collect { domainResult ->
                when (domainResult) {
                    is DomainResult.Success -> {
                        val alarmGroups = domainResult.data.map { domainTempGroup ->
                            alarmGroupMapper.mapToPresentation(domainTempGroup)
                        }
                        setState {
                            copy(alarmGroupList = alarmGroups)
                        }
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
            is AlarmMainAction.Add -> {
                addAlarmGroup(action.alarmGroup)
            }

            is AlarmMainAction.Update -> {
                updateAlarmGroup(action.alarmGroup)
            }
            is AlarmMainAction.Delete -> {
                deleteAlarmGroup(action.alarmGroup)
            }

            is AlarmMainAction.DeleteSelected -> {
                deleteSelectedAlarmGroups(action.alarmGroups)
            }

            is AlarmMainAction.SortChange -> {
                postEffect(
                    AlarmMainSideEffect.ShowSortBottomSheet(
                        sort = action.sort
                    )
                )
                setState {
                    copy(sort = action.sort)
                }
                currentSort.value = action.sort
            }
        }
    }

    private fun addAlarmGroup(alarmGroup: AlarmGroupModel) {
        val domainAlarmGroup = alarmGroupMapper.mapToDomain(alarmGroup)

        viewModelScope.launch {
            addAlarmGroupUseCase(domainAlarmGroup)
        }
    }

    private fun updateAlarmGroup(alarmGroup: AlarmGroupModel) {
        val domainAlarmGroup = alarmGroupMapper.mapToDomain(alarmGroup)

        viewModelScope.launch {
            updateAlarmGroupUseCase(domainAlarmGroup)
        }
    }

    private fun deleteAlarmGroup(alarmGroup: AlarmGroupModel) {
        val domainAlarmGroup = alarmGroupMapper.mapToDomain(alarmGroup)

        viewModelScope.launch {
            deleteAlarmGroupUseCase(domainAlarmGroup)
        }
    }

    private fun deleteSelectedAlarmGroups(alarmGroups: List<AlarmGroupModel>) {
        val domainAlarmGroups = alarmGroups.map { group ->
            alarmGroupMapper.mapToDomain(group)
        }

        viewModelScope.launch {
            val deferredJobs = domainAlarmGroups.map { group ->
                async { deleteAlarmGroupUseCase(group) }
            }
            deferredJobs.awaitAll()
        }
    }
}