package dev.loki.dog.feature.main

import dev.loki.alarm.usecase.AddAlarmUseCase
import dev.loki.alarmgroup.usecase.DeleteAlarmGroupUseCase
import dev.loki.alarmgroup.usecase.DeleteSelectedAlarmGroupsUseCase
import dev.loki.alarmgroup.usecase.GetAlarmGroupsUseCase
import dev.loki.alarmgroup.usecase.UpdateAlarmGroupUseCase
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.feature.base.BaseAction
import dev.loki.dog.feature.base.BaseStore
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope

class AlarmMainStore(
    coroutineScope: CoroutineScope,
    scope: Scope
): BaseStore<AlarmMainState, AlarmMainSideEffect>(
    coroutineScope = coroutineScope,
    scope = scope,
    initialState = AlarmMainState()
) {
    private val getAlarmGroupsUseCase: GetAlarmGroupsUseCase by inject()
    private val addAlarmUseCase: AddAlarmUseCase by inject()
    private val updateAlarmGroupUseCase: UpdateAlarmGroupUseCase by inject()
    private val deleteAlarmGroupUseCase: DeleteAlarmGroupUseCase by inject()
    private val deleteSelectedAlarmGroupUseCase: DeleteSelectedAlarmGroupsUseCase by inject()

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
        }
    }

    private fun addAlarmGroup(alarmGroup: AlarmGroupModel) {

    }

    private fun updateAlarmGroup(alarmGroup: AlarmGroupModel) {

    }

    private fun deleteAlarmGroup(alarmGroup: AlarmGroupModel) {

    }

    private fun deleteSelectedAlarmGroups(alarmGroups: List<AlarmGroupModel>) {

    }
}