package dev.loki.timerhistory.usecase

import dev.loki.timerhistory.model.TimerHistory
import dev.loki.timerhistory.repository.TimerHistoryRepository

class CreateTimerHistoryUseCase(
    private val repository: TimerHistoryRepository
) {

    suspend operator fun invoke(history: TimerHistory) {
        repository.createTimerHistory(history)
    }
}