package dev.loki.dog.feature.addalarmgroup

import dev.loki.dog.feature.base.BaseSharedViewModel
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.model.AlarmModel

class AddAlarmGroupViewModel(
    factory: AddAlarmGroupStoreFactory
): BaseSharedViewModel<AddAlarmGroupState, AddAlarmGroupSideEffect>(
    factory = factory
) {

    fun getAlarmGroup(id: Long) {
        dispatch(AddAlarmGroupAction.GetAlarmGroup(id))
    }

    fun saveAlarmGroup(alarmGroup: AlarmGroupModel) {
        dispatch(AddAlarmGroupAction.Save(alarmGroup))
    }

    fun saveTempAlarmGroup(alarmGroup: AlarmGroupModel) {
        dispatch(AddAlarmGroupAction.SaveTemp(alarmGroup))
    }

    fun deleteAlarm(alarm: AlarmModel) {
        dispatch(AddAlarmGroupAction.DeleteAlarm(alarm))
    }

    fun getTempAlarmGroup() {
        dispatch(AddAlarmGroupAction.GetTempAlarmGroup)
    }
}