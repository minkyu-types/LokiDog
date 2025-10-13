package dev.loki.dog.feature.temp

import dev.loki.dog.feature.base.BaseSideEffect
import dev.loki.dog.model.AlarmGroupModel

sealed class TempAlarmGroupsSideEffect: BaseSideEffect {
    data class ShowDeleteDialog(val alarmGroup: AlarmGroupModel): TempAlarmGroupsSideEffect()
    data class ShowUpdateDialog(val alarmGroup: AlarmGroupModel): TempAlarmGroupsSideEffect()
}