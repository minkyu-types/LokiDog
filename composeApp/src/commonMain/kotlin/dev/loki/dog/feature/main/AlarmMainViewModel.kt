package dev.loki.dog.feature.main

import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.feature.base.BaseSharedViewModel

class AlarmMainViewModel(
    factory: AlarmMainStoreFactory
) : BaseSharedViewModel<AlarmMainState, AlarmMainSideEffect>(
    factory = factory
) {

    init {

    }

    fun addAlarmGroup(alarmGroup: AlarmGroupModel) {
        dispatch(AlarmMainAction.Add(alarmGroup))
    }

    fun updateAlarmGroup(alarmGroup: AlarmGroupModel) {
        dispatch(AlarmMainAction.Update(alarmGroup))
    }

    fun deleteAlarmGroup(alarmGroup: AlarmGroupModel) {
        dispatch(AlarmMainAction.Delete(alarmGroup))
    }

    fun deleteSelectedAlarmGroups(alarmGroups: List<AlarmGroupModel>) {
        dispatch(AlarmMainAction.DeleteSelected(alarmGroups))
    }
}