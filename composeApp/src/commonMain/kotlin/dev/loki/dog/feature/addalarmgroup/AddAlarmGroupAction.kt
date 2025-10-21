package dev.loki.dog.feature.addalarmgroup

import dev.loki.dog.feature.base.BaseAction
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.model.AlarmModel

sealed class AddAlarmGroupAction: BaseAction {
    data class GetAlarmGroup(val id: Long) : AddAlarmGroupAction()
    data class Save(val alarmGroup: AlarmGroupModel) : AddAlarmGroupAction()
    data class SaveTemp(val alarmGroup: AlarmGroupModel) : AddAlarmGroupAction()
    data class UpdateAlarm(val alarmGroup: AlarmGroupModel, val alarm: AlarmModel) : AddAlarmGroupAction()
    data class DeleteAlarm(val alarm: AlarmModel) : AddAlarmGroupAction()
    data object GetTempAlarmGroup : AddAlarmGroupAction()
}