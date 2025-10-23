package dev.loki.dog.feature.addalarmgroup

import dev.loki.dog.feature.base.BaseState
import dev.loki.dog.feature.base.LoadState
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.model.AlarmModel

data class AddAlarmGroupState(
    override val loadState: LoadState = LoadState.Idle,
    val alarmGroup: AlarmGroupModel = AlarmGroupModel.createTemp(),
    val alarms: List<AlarmModel> = emptyList()
): BaseState