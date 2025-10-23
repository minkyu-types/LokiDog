package dev.loki.dog.feature.addalarmgroup

import dev.loki.dog.feature.base.BaseSharedViewModel
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.model.AlarmModel
import kotlinx.datetime.DayOfWeek

class AddAlarmGroupViewModel(
    factory: AddAlarmGroupStoreFactory
): BaseSharedViewModel<AddAlarmGroupState, AddAlarmGroupSideEffect>(
    factory = factory
) {

    fun getAlarmGroup(id: Long) {
        dispatch(AddAlarmGroupAction.GetAlarmGroup(id))
    }

    fun rescheduleAlarm(isActivated: Boolean, repeatDays: Set<DayOfWeek>, alarms: List<AlarmModel>) {
        dispatch(AddAlarmGroupAction.Reschedule(repeatDays, alarms))
    }

    fun saveAlarmGroup(alarmGroup: AlarmGroupModel, alarms: List<AlarmModel>) {
        dispatch(AddAlarmGroupAction.SaveAlarmGroup(alarmGroup, alarms))
    }

    fun saveTempAlarmGroup(alarmGroup: AlarmGroupModel, alarms: List<AlarmModel>) {
        dispatch(AddAlarmGroupAction.SaveTempAlarmGroup(alarmGroup, alarms))
    }

    fun upsertAlarm(alarmGroup: AlarmGroupModel, alarm: AlarmModel) {
        dispatch(AddAlarmGroupAction.UpdateAlarm(alarmGroup, alarm))
    }

    fun deleteAlarm(alarm: AlarmModel) {
        dispatch(AddAlarmGroupAction.DeleteAlarm(alarm))
    }

    fun getTempAlarmGroup() {
        dispatch(AddAlarmGroupAction.GetTempAlarmGroup)
    }
}