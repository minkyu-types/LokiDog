package dev.loki.alarm_data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Upsert
import dev.loki.alarm_data.model.AlarmEntity

@Dao
interface AlarmDao {

    @Insert
    suspend fun insert(alarm: AlarmEntity)

    @Upsert
    suspend fun upsert(alarm: AlarmEntity)

    @Delete
    suspend fun delete(alarm: AlarmEntity)
}