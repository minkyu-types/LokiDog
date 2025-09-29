package dev.loki.dog.feature.main

import dev.loki.alarm.usecase.AddAlarmUseCase
import dev.loki.alarmgroup.usecase.saved.DeleteAlarmGroupUseCase
import dev.loki.alarmgroup.usecase.saved.DeleteSelectedAlarmGroupsUseCase
import dev.loki.alarmgroup.usecase.saved.UpdateAlarmGroupUseCase
import dev.loki.dog.feature.alarmgroup.AlarmGroupModel
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