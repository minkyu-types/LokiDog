package dev.loki.alarm_data.repository

import dev.loki.alarm_data.dao.TimerHistoryDao
import dev.loki.alarm_data.mapper.TimerHistoryMapper
import dev.loki.timerhistory.model.TimerHistory
import dev.loki.timerhistory.repository.TimerHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TimerHistoryRepositoryImpl(
    private val timerHistoryDao: TimerHistoryDao,
    private val timerHistoryMapper: TimerHistoryMapper
): TimerHistoryRepository {
    override fun getTimerHistories(): Flow<List<TimerHistory>> {
        return timerHistoryDao.getTimerHistories()
            .map {
                it.map { history ->
                    timerHistoryMapper.mapToDomain(history)
                }
            }
    }

    override suspend fun createTimerHistory(history: TimerHistory) {
        val dataHistory = timerHistoryMapper.mapToData(history)
        timerHistoryDao.insert(dataHistory)
    }

    override suspend fun deleteTimerHistory(history: TimerHistory) {
        val dataHistory = timerHistoryMapper.mapToData(history)
        timerHistoryDao.delete(dataHistory)
    }
}