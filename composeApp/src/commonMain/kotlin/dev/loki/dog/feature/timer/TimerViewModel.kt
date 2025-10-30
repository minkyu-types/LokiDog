package dev.loki.dog.feature.timer

import dev.loki.dog.feature.base.BaseSharedViewModel

class TimerViewModel(
    factory: TimerStoreFactory
): BaseSharedViewModel<TimerState, TimerSideEffect>(
    factory = factory,
) {
    fun setTimerTime(time: Long) {
        dispatch(TimerAction.SetTime(time))
    }

    fun startTimer() {
        dispatch(TimerAction.Start)
    }

    fun pauseTimer() {
        dispatch(TimerAction.Pause)
    }

    fun resumeTimer() {
        dispatch(TimerAction.Resume)
    }

    fun quitTimer() {
        dispatch(TimerAction.Quit)
    }

    fun deleteHistory(historyModel: TimerHistoryModel) {
        dispatch(TimerAction.DeleteHistory(historyModel))
    }
}