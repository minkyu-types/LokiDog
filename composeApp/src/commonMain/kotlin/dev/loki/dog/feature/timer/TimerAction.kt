package dev.loki.dog.feature.timer

import dev.loki.dog.feature.base.BaseAction

sealed class TimerAction: BaseAction {
    data class SetTime(val time: Long): TimerAction()
    data object Start: TimerAction()
    data object Pause: TimerAction()
    data object Resume: TimerAction()
    data object Quit: TimerAction()
    data class DeleteHistory(val history: TimerHistoryModel): TimerAction()
}