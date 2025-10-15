package dev.loki.dog.feature.addalarmgroup

import dev.loki.DomainResult
import dev.loki.alarm.usecase.DeleteAlarmUseCase
import dev.loki.alarm.usecase.UpsertAlarmUseCase
import dev.loki.alarmgroup.usecase.GetAlarmGroupWithAlarmsUseCase
import dev.loki.alarmgroup.usecase.UpsertAlarmGroupUseCase
import dev.loki.dog.feature.base.BaseAction
import dev.loki.dog.feature.base.BaseStore
import dev.loki.dog.feature.base.LoadState
import dev.loki.dog.mapper.AlarmGroupMapper
import dev.loki.dog.mapper.AlarmMapper
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.model.AlarmModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.scope.Scope

class AddAlarmGroupStore(
    private val coroutineScope: CoroutineScope,
    scope: Scope
) : BaseStore<AddAlarmGroupState, AddAlarmGroupSideEffect>(
    coroutineScope = coroutineScope,
    scope = scope,
    initialState = AddAlarmGroupState()
) {
    private val getAlarmGroupWithAlarmsUseCase: GetAlarmGroupWithAlarmsUseCase by inject()
    private val upsertAlarmGroupUseCase: UpsertAlarmGroupUseCase by inject()
    private val alarmMapper: AlarmMapper by inject()
    private val alarmGroupMapper: AlarmGroupMapper by inject()

    private val upsertAlarmUseCase: UpsertAlarmUseCase by inject()
    private val deleteAlarmUseCase: DeleteAlarmUseCase by inject()

    override fun dispatch(action: BaseAction) {
        when (action) {
            is AddAlarmGroupAction.GetAlarmGroup -> {
                getAlarmGroupWithAlarms(action.id)
            }

            is AddAlarmGroupAction.Save -> {
                saveAlarmGroup(action.alarmGroup)
            }

            is AddAlarmGroupAction.SaveTemp -> {
                saveTempAlarmGroup(action.alarmGroup)
            }

            is AddAlarmGroupAction.UpdateAlarm -> {
                upsertAlarm(action.alarm)
            }

            is AddAlarmGroupAction.DeleteAlarm -> {
                deleteAlarm(action.alarm)
            }

            is AddAlarmGroupAction.GetTempAlarmGroup -> {
                setState {
                    copy(
                        tempAlarmGroup = AlarmGroupModel.createTemp()
                    )
                }
            }
        }
    }

    private fun getAlarmGroupWithAlarms(groupId: Long) {
        viewModelScope.launch {
            getAlarmGroupWithAlarmsUseCase(groupId).collect { domainResult ->
                when (domainResult) {
                    is DomainResult.Success -> {
                        val alarmGroup = alarmGroupMapper.mapToPresentation(domainResult.data.group)
                        val alarms = domainResult.data.alarms.map {
                            alarmMapper.mapToPresentation(it)
                        }

                        setState {
                            copy(
                                tempAlarmGroup = alarmGroup.copy(
                                    alarms = alarms
                                )
                            )
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

    private fun saveAlarmGroup(alarmGroup: AlarmGroupModel) {
        val domainAlarmGroup = alarmGroupMapper.mapToDomain(alarmGroup).copy(isTemp = false)
        val domainAlarms = alarmGroup.alarms.map {
            alarmMapper.mapToDomain(it).copy(
                groupId = domainAlarmGroup.id,
                isTemp = false
            )
        }

        viewModelScope.launch {
            val groupId = upsertAlarmGroupUseCase(domainAlarmGroup)

            val alarmsWithGroupId = domainAlarms.map {
                it.copy(groupId = groupId)
            }

            alarmsWithGroupId.forEach { upsertAlarmUseCase(it) }
        }
    }

    private fun saveTempAlarmGroup(alarmGroup: AlarmGroupModel) {
        val domainTempAlarmGroup = alarmGroupMapper.mapToDomain(alarmGroup).copy(isTemp = true)
        val domainAlarms = alarmGroup.alarms.map { alarmMapper.mapToDomain(it).copy(isTemp = true) }

        viewModelScope.launch {
            val groupId = upsertAlarmGroupUseCase(domainTempAlarmGroup)

            val alarmJobs = domainAlarms.map {
                it.copy(groupId = groupId)
            }

            alarmJobs.forEach { upsertAlarmUseCase(it) }
        }
    }

    private fun upsertAlarm(alarm: AlarmModel) {
        val domainAlarm = alarmMapper.mapToDomain(alarm)

        viewModelScope.launch {
            upsertAlarmUseCase(domainAlarm)
        }
    }

    private fun deleteAlarm(alarm: AlarmModel) {
        val domainAlarm = alarmMapper.mapToDomain(alarm)

        viewModelScope.launch {
            deleteAlarmUseCase(domainAlarm)
        }
    }
}