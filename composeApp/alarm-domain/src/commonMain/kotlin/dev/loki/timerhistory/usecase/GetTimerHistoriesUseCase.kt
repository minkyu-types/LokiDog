package dev.loki.timerhistory.usecase

import dev.loki.timerhistory.repository.TimerHistoryRepository
import dev.loki.timerhistory.model.TimerHistory
import kotlinx.coroutines.flow.Flow

class GetTimerHistoriesUseCase(
    private val repository: TimerHistoryRepository
) {

    operator fun invoke(): Flow<List<TimerHistory>> {
        return repository.getTimerHistories()
    }
}