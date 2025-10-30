package dev.loki.timerhistory.usecase

import dev.loki.timerhistory.model.TimerHistory
import dev.loki.timerhistory.repository.TimerHistoryRepository

class DeleteTimerHistoryUseCase(
    private val repository: TimerHistoryRepository
) {

    suspend operator fun invoke(history: TimerHistory) {
        repository.deleteTimerHistory(history)
    }
}