package dev.loki.alarm_data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.loki.alarm_data.model.AlarmGroupEntity
import dev.loki.alarmgroup.model.AlarmMainSort
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmGroupDao {

    @Query("SELECT * FROM alarm_group ORDER BY createdAt DESC")
    fun getAlarmGroupsByCreated(sort: AlarmMainSort): Flow<List<AlarmGroupEntity>>

    @Query("SELECT * FROM alarm_group ORDER BY updatedAt DESC")
    fun getAlarmGroupsByUpdated(sort: AlarmMainSort): Flow<List<AlarmGroupEntity>>

    @Query("SELECT * FROM alarm_group ORDER BY isActivated DESC, created DESC")
    fun getAlarmGroupsByActivated(sort: AlarmMainSort): Flow<List<AlarmGroupEntity>>

    @Query("SELECT * FROM alarm_group WHERE (:isTemp) ORDER BY updated DESC")
    fun getTempAlarmGroups(): Flow<List<AlarmGroupEntity>>

    @Insert
    suspend fun insert(item: AlarmGroupEntity)

    @Update
    suspend fun update(item: AlarmGroupEntity)

    @Delete
    suspend fun delete(item: AlarmGroupEntity)

    @Query("DELETE FROM alarm_group WHERE id IN (:items)")
    suspend fun deleteSelectedAlarmGroups(items: List<AlarmGroupEntity>)
}