package dev.loki.dog.feature.main

import dev.loki.alarmgroup.model.AlarmMainSort
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.feature.base.BaseAction

sealed class AlarmMainAction: BaseAction {
    data class SortChange(val sort: AlarmMainSort): AlarmMainAction()
    data class Add(val alarmGroup: AlarmGroupModel): AlarmMainAction()
    data class Update(val alarmGroup: AlarmGroupModel): AlarmMainAction()
    data class Delete(val alarmGroup: AlarmGroupModel): AlarmMainAction()
    data class DeleteSelected(val alarmGroups: List<AlarmGroupModel>): AlarmMainAction()
    data object SignOut: AlarmMainAction()
}