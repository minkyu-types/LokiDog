package dev.loki.dog.feature.main

import dev.loki.alarmgroup.model.AlarmMainSort
import dev.loki.dog.feature.alarmgroup.AlarmGroupModel
import dev.loki.dog.feature.base.BaseState
import dev.loki.dog.feature.base.LoadState

data class AlarmMainState(
    override val loadState: LoadState = LoadState.Idle,
    val morningAlarmGroupList: List<AlarmGroupModel> = emptyList(),
    val alarmGroupList: List<AlarmGroupModel> = emptyList(),
    val sort: AlarmMainSort = AlarmMainSort.ACTIVATED_FIRST,
): BaseState
