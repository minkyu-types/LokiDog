package dev.loki.dog.feature.temp

import dev.loki.dog.feature.base.BaseAction
import dev.loki.dog.model.AlarmGroupModel

sealed class TempAlarmGroupsAction: BaseAction {
    data class Delete(val alarmGroup: AlarmGroupModel): TempAlarmGroupsAction() // 삭제
    data class Update(val alarmGroup: AlarmGroupModel): TempAlarmGroupsAction() // 업데이트(여전히 임시 저장 상태)
    data class Save(val alarmGroup: AlarmGroupModel): TempAlarmGroupsAction() // 저장(알람 그룹으로 승격)
}