package dev.loki.dog.feature.temp

import dev.loki.dog.feature.base.BaseState
import dev.loki.dog.feature.base.LoadState
import dev.loki.dog.model.AlarmGroupModel

data class TempAlarmGroupsState(
    override val loadState: LoadState = LoadState.Idle,
    val tempAlarmGroupList: List<AlarmGroupModel> = emptyList(),
): BaseState