package dev.loki.timerhistory.repository

import dev.loki.timerhistory.model.TimerHistory
import kotlinx.coroutines.flow.Flow

interface TimerHistoryRepository {

    fun getTimerHistories(): Flow<List<TimerHistory>>
    suspend fun createTimerHistory(history: TimerHistory)
    suspend fun deleteTimerHistory(history: TimerHistory)
}