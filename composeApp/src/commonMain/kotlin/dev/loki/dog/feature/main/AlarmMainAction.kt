package dev.loki.dog.feature.main

import dev.loki.dog.feature.alarmgroup.AlarmGroupModel
import dev.loki.dog.feature.base.BaseAction

sealed class AlarmMainAction: BaseAction {
    data class Add(val alarmGroup: AlarmGroupModel): AlarmMainAction()
    data class Update(val alarmGroup: AlarmGroupModel): AlarmMainAction()
    data class Delete(val alarmGroup: AlarmGroupModel): AlarmMainAction()
    data class DeleteSelected(val alarmGroups: List<AlarmGroupModel>): AlarmMainAction()
}