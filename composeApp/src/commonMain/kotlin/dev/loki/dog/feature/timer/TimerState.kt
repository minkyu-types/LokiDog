package dev.loki.dog.feature.timer

import dev.loki.dog.feature.base.BaseState
import dev.loki.dog.feature.base.LoadState

data class TimerState(
    override val loadState: LoadState = LoadState.Success,
    val timerHistories: List<TimerHistoryModel> = emptyList(),
    var totalDuration: Long,
    var remainingTime: Long,
    var isRunning: Boolean = false,
    var isPaused: Boolean = false,
    var pausedAt: Long = 0L,
    var alarmTime: Long = 0L
): BaseState