package dev.loki.dog.feature.temp

import dev.loki.dog.feature.base.BaseSharedViewModel
import dev.loki.dog.model.AlarmGroupModel

class TempAlarmGroupsViewModel(
    factory: TempAlarmGroupsStoreFactory
): BaseSharedViewModel<TempAlarmGroupsState, TempAlarmGroupsSideEffect>(
    factory = factory
) {
    fun saveTempAlarmGroup(alarmGroup: AlarmGroupModel) {
        dispatch(TempAlarmGroupsAction.Save(alarmGroup))
    }

    fun updateTempAlarmGroup(alarmGroup: AlarmGroupModel) {
        dispatch(TempAlarmGroupsAction.Update(alarmGroup))
    }

    fun deleteTempAlarmGroup(alarmGroup: AlarmGroupModel) {
        dispatch(TempAlarmGroupsAction.Delete(alarmGroup))
    }
}