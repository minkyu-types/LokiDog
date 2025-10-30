package dev.loki.alarm_data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import dev.loki.alarm_data.model.TimerHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerHistoryDao {

    @Query("SELECT * FROM timerHistory ORDER BY id DESC")
    fun getTimerHistories(): Flow<List<TimerHistoryEntity>>

    @Insert
    suspend fun insert(historyEntity: TimerHistoryEntity)

    @Delete
    suspend fun delete(historyEntity: TimerHistoryEntity)
}