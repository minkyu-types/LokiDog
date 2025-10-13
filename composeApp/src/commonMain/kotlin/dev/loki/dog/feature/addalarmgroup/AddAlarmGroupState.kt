package dev.loki.dog.feature.addalarmgroup

import dev.loki.dog.feature.base.BaseState
import dev.loki.dog.feature.base.LoadState
import dev.loki.dog.model.AlarmGroupModel

data class AddAlarmGroupState(
    override val loadState: LoadState = LoadState.Idle,
    val tempAlarmGroup: AlarmGroupModel = AlarmGroupModel.createTemp(),
): BaseState