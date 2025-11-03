package dev.loki.dog.feature.main

import dev.loki.alarmgroup.model.AlarmMainSort
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.feature.base.BaseState
import dev.loki.dog.feature.base.LoadState

data class AlarmMainState(
    override val loadState: LoadState = LoadState.Idle,
    val morningAlarmGroupList: List<AlarmGroupModel> = emptyList(),
    val alarmGroupList: List<AlarmGroupModel> = emptyList(),
    val tempAlarmSize: Int = 0,
    val sort: AlarmMainSort = AlarmMainSort.MOST_RECENT_CREATED,
): BaseState
