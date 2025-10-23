package dev.loki.alarm_data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import dev.loki.alarm_data.model.AlarmEntity

@Dao
interface AlarmDao {

    @Query("SELECT * FROM alarm WHERE id = :id")
    suspend fun getAlarmById(id: Long): AlarmEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: AlarmEntity)

    @Upsert
    suspend fun upsert(alarm: AlarmEntity): Long

    @Delete
    suspend fun delete(alarm: AlarmEntity)

    @Query("SELECT * FROM alarm WHERE groupId = :groupId")
    suspend fun getAlarmsByGroupId(groupId: Long): List<AlarmEntity>

    @Query("SELECT COUNT(*) FROM alarm WHERE groupId = :groupId")
    suspend fun getAlarmCountByGroup(groupId: Long): Int
}