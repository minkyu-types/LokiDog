package dev.loki.dog.feature.addalarmgroup

import dev.loki.dog.feature.base.BaseAction
import dev.loki.dog.model.AlarmGroupModel

sealed class AddAlarmGroupAction: BaseAction {
    data class Save(val alarmGroup: AlarmGroupModel) : AddAlarmGroupAction()
    data class SaveTemp(val alarmGroup: AlarmGroupModel) : AddAlarmGroupAction()
}