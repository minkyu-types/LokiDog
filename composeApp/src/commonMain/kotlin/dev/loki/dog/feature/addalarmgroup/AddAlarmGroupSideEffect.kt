package dev.loki.dog.feature.addalarmgroup

import dev.loki.dog.feature.base.BaseSideEffect
import dev.loki.dog.model.AlarmGroupModel

sealed class AddAlarmGroupSideEffect: BaseSideEffect {
    data class ShowSaveTempAlarmGroupDialog(val alarmGroup: AlarmGroupModel)
}