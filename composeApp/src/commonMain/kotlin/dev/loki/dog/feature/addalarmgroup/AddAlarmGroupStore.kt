package dev.loki.dog.feature.addalarmgroup

import co.touchlab.kermit.Logger
import dev.loki.DomainResult
import dev.loki.alarm.usecase.DeleteAlarmUseCase
import dev.loki.alarm.usecase.GetAlarmByIdUseCase
import dev.loki.alarm.usecase.GetAlarmsByGroupIdUseCase
import dev.loki.alarm.usecase.RescheduleAlarmUseCase
import dev.loki.alarm.usecase.UpsertAlarmUseCase
import dev.loki.alarmgroup.usecase.GetAlarmGroupById
import dev.loki.alarmgroup.usecase.GetAlarmGroupWithAlarmsUseCase
import dev.loki.alarmgroup.usecase.InsertAlarmGroupUseCase
import dev.loki.alarmgroup.usecase.UpdateAlarmGroupUseCase
import dev.loki.dog.feature.base.BaseAction
import dev.loki.dog.feature.base.BaseStore
import dev.loki.dog.feature.base.LoadState
import dev.loki.dog.mapper.AlarmGroupMapper
import dev.loki.dog.mapper.AlarmMapper
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.model.AlarmModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
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
    private var getAlarmGroupJob: Job? = null
    private val getAlarmGroupWithAlarmsUseCase: GetAlarmGroupWithAlarmsUseCase by inject()
    private val getAlarmGroupById: GetAlarmGroupById by inject()
    private val insertAlarmGroupUseCase: InsertAlarmGroupUseCase by inject()
    private val updateAlarmGroupUseCase: UpdateAlarmGroupUseCase by inject()
    private val alarmMapper: AlarmMapper by inject()
    private val alarmGroupMapper: AlarmGroupMapper by inject()

    private val getAlarmByIdUseCase: GetAlarmByIdUseCase by inject()
    private val getAlarmsByGroupIdUseCase: GetAlarmsByGroupIdUseCase by inject()
    private val rescheduleAlarmUseCase: RescheduleAlarmUseCase by inject()
    private val upsertAlarmUseCase: UpsertAlarmUseCase by inject()
    private val deleteAlarmUseCase: DeleteAlarmUseCase by inject()

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("예외 발생: ${exception.message}")
        exception.printStackTrace()
    }

    override fun dispatch(action: BaseAction) {
        when (action) {
            is AddAlarmGroupAction.GetAlarmGroup -> {
                getAlarmGroupWithAlarms(action.id)
            }

            is AddAlarmGroupAction.SaveAlarmGroup -> {
                viewModelScope.launch(exceptionHandler) {
                    if (action.alarmGroup.id == 0L || action.alarmGroup.isTemp) {
                        // 신규 또는 임시 그룹 -> insert
                        val group = saveAlarmGroup(action.alarmGroup)
                        action.alarms.forEach {
                            upsertAlarm(action.alarmGroup, it.copy(groupId = group.id))
                        }
                    } else {
                        // 기존 그룹 -> update
                        updateAlarmGroup(action.alarmGroup)
                    }
                }
            }

            is AddAlarmGroupAction.SaveTempAlarmGroup -> {
                viewModelScope.launch {
                    val group = saveTempAlarmGroup(action.alarmGroup)
                    action.alarms.forEach {
                        val alarm = alarmMapper.mapToDomain(it)
                        upsertAlarmUseCase(group.repeatDays, alarm.copy(groupId = group.id))
                    }
                }
            }

            is AddAlarmGroupAction.UpdateAlarm -> {
                upsertAlarm(action.alarmGroup, action.alarm)
            }

            is AddAlarmGroupAction.DeleteAlarm -> {
                deleteAlarm(action.alarm)
            }

            is AddAlarmGroupAction.GetTempAlarmGroup -> {
                setState {
                    copy(
                        alarmGroup = AlarmGroupModel.createTemp(),
                        alarms = emptyList()
                    )
                }
            }
        }
    }

    private fun getAlarmGroupWithAlarms(groupId: Long) {
        getAlarmGroupJob?.cancel()

        getAlarmGroupJob = viewModelScope.launch {
            getAlarmGroupWithAlarmsUseCase(groupId).collect { domainResult ->
                when (domainResult) {
                    is DomainResult.Success -> {
                        // groupId가 현재 요청한 것과 일치하는지 확인
                        if (domainResult.data.group.id == groupId) {
                            val alarmGroup = alarmGroupMapper.mapToPresentation(domainResult.data.group)
                            val alarms = domainResult.data.alarms.map {
                                alarmMapper.mapToPresentation(it)
                            }

                            setState {
                                copy(
                                    alarmGroup = alarmGroup,
                                    alarms = alarms
                                )
                            }
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

    /**
     * 저장
     * - 최초 1회
     * - 임시저장된 알람 그룹을 isTemp를 false로 설정하고 저장
     */
    private suspend fun saveAlarmGroup(alarmGroup: AlarmGroupModel): AlarmGroupModel {
        val domainAlarmGroup = alarmGroupMapper.mapToDomain(alarmGroup).copy(isTemp = false)
        val groupId = insertAlarmGroupUseCase(domainAlarmGroup)
        return alarmGroup.copy(id = groupId)
    }

    // 임시 저장
    private suspend fun saveTempAlarmGroup(alarmGroup: AlarmGroupModel): AlarmGroupModel {
        val domainTempAlarmGroup = alarmGroupMapper.mapToDomain(alarmGroup).copy(isTemp = true)
        val groupId = insertAlarmGroupUseCase(domainTempAlarmGroup)
        return alarmGroup.copy(id = groupId)
    }

    private suspend fun updateAlarmGroup(
        alarmGroup: AlarmGroupModel,
    ) {
        val prevData = getAlarmGroupById(alarmGroup.id) ?: return
        val domainAlarmGroup = alarmGroupMapper.mapToDomain(alarmGroup)
        updateAlarmGroupUseCase(domainAlarmGroup)

        if (
            prevData.repeatDays != domainAlarmGroup.repeatDays ||
            prevData.isActivated != domainAlarmGroup.isActivated
        ) {
            rescheduleAlarms(
                groupId = domainAlarmGroup.id,
                isActivated = domainAlarmGroup.isActivated,
                repeatDays = domainAlarmGroup.repeatDays,
            )
        }
    }

    /**
     * 그룹에 포함된 모든 알람 reschedule
     */
    private suspend fun rescheduleAlarms(groupId: Long, isActivated: Boolean, repeatDays: Set<DayOfWeek>) {
        val alarms = getAlarmsByGroupIdUseCase(groupId)

        alarms.forEach { alarm ->
            rescheduleAlarmUseCase(isActivated, repeatDays, alarm)
        }
    }

    private suspend fun rescheduleAlarm(isActivated: Boolean, repeatDays: Set<DayOfWeek>, alarm: AlarmModel) {
        val domainAlarm = alarmMapper.mapToDomain(alarm)
        rescheduleAlarmUseCase(isActivated, repeatDays, domainAlarm)
    }

    /**
     * 해당 알람 reschedule 포함
     */
    private fun upsertAlarm(alarmGroup: AlarmGroupModel, alarm: AlarmModel) {
        val domainAlarm = alarmMapper.mapToDomain(alarm)

        viewModelScope.launch {
            val prevAlarmData = getAlarmByIdUseCase(alarm.id)
            upsertAlarmUseCase(alarmGroup.repeatDays, domainAlarm)
            if (prevAlarmData != null) {
                if (
                    prevAlarmData.time != domainAlarm.time ||
                    prevAlarmData.isActivated != domainAlarm.isActivated
                ) {
                    rescheduleAlarm(domainAlarm.isActivated, alarmGroup.repeatDays, alarm)
                }
            } else {
                rescheduleAlarm(domainAlarm.isActivated, alarmGroup.repeatDays, alarm)
            }
        }
    }

    private fun deleteAlarm(alarm: AlarmModel) {
        val domainAlarm = alarmMapper.mapToDomain(alarm)

        viewModelScope.launch {
            deleteAlarmUseCase(domainAlarm)
        }
    }
}