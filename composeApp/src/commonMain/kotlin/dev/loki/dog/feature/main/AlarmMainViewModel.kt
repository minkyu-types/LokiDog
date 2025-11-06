package dev.loki.dog.feature.main

import dev.loki.alarmgroup.model.AlarmMainSort
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.feature.base.BaseSharedViewModel

class AlarmMainViewModel(
    factory: AlarmMainStoreFactory
) : BaseSharedViewModel<AlarmMainState, AlarmMainSideEffect>(
    factory = factory
) {

    fun updateSort(sort: AlarmMainSort) {
        dispatch(AlarmMainAction.SortChange(sort))
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

    fun showSortBottomSheet(sort: AlarmMainSort) {
        dispatch(AlarmMainAction.SortChange(sort))
    }

    fun signOut() {
        dispatch(AlarmMainAction.SignOut)
    }
}