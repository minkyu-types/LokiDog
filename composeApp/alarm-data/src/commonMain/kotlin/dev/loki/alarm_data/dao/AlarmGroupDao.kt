package dev.loki.alarm_data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.loki.alarm_data.model.AlarmGroupEntity
import dev.loki.alarm_data.model.AlarmGroupWithAlarms
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmGroupDao {

    @Query("SELECT * FROM alarm_group WHERE isTemp = 0 ORDER BY createdAt DESC")
    fun getAlarmGroupsByCreated(): Flow<List<AlarmGroupEntity>>

    @Query("SELECT * FROM alarm_group WHERE isTemp = 0 ORDER BY updatedAt DESC")
    fun getAlarmGroupsByUpdated(): Flow<List<AlarmGroupEntity>>

    @Query("SELECT * FROM alarm_group WHERE isTemp = 0 ORDER BY isActivated DESC, createdAt DESC")
    fun getAlarmGroupsByActivated(): Flow<List<AlarmGroupEntity>>

    @Query("SELECT * FROM alarm_group WHERE isTemp = 0 ORDER  BY title DESC")
    fun getAlarmGroupsByAlphabet(): Flow<List<AlarmGroupEntity>>

    @Query("SELECT * FROM alarm_group WHERE id = :id")
    fun getAlarmGroupWithAlarms(id: Long): Flow<AlarmGroupWithAlarms>

    @Query("SELECT * FROM alarm_group WHERE isTemp = 1 ORDER BY updatedAt DESC")
    fun getTempAlarmGroups(): Flow<List<AlarmGroupEntity>>

    @Insert
    suspend fun insert(item: AlarmGroupEntity): Long

    @Update
    suspend fun update(item: AlarmGroupEntity)

    @Delete
    suspend fun delete(item: AlarmGroupEntity)

    @Query("DELETE FROM alarm_group WHERE id IN (:ids)")
    suspend fun deleteSelectedAlarmGroups(ids: List<Long>)
}