package dev.loki.dog.feature.addalarmgroup

import dev.loki.dog.feature.base.BaseAction
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.model.AlarmModel
import kotlinx.datetime.DayOfWeek

sealed class AddAlarmGroupAction: BaseAction {
    data class GetAlarmGroup(val id: Long) : AddAlarmGroupAction()
    data class Reschedule(val repeatDays: Set<DayOfWeek>, val alarms: List<AlarmModel>) : AddAlarmGroupAction()
    data class SaveAlarmGroup(val alarmGroup: AlarmGroupModel, val alarms: List<AlarmModel>) : AddAlarmGroupAction()
    data class SaveTempAlarmGroup(val alarmGroup: AlarmGroupModel, val alarms: List<AlarmModel>) : AddAlarmGroupAction()
    data class UpdateAlarm(val alarmGroup: AlarmGroupModel, val alarm: AlarmModel) : AddAlarmGroupAction()
    data class DeleteAlarm(val alarm: AlarmModel) : AddAlarmGroupAction()
    data object GetTempAlarmGroup : AddAlarmGroupAction()
}