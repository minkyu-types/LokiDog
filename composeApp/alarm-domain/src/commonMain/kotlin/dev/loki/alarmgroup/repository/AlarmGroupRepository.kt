package dev.loki.alarmgroup.repository

import dev.loki.alarmgroup.model.AlarmGroup
import dev.loki.alarmgroup.model.AlarmGroupWithAlarms
import dev.loki.alarmgroup.model.AlarmMainSort
import kotlinx.coroutines.flow.Flow

interface AlarmGroupRepository {

    fun getAlarmGroupWithAlarms(id: Long): Flow<AlarmGroupWithAlarms>
    fun getAlarmGroups(sort: AlarmMainSort): Flow<List<AlarmGroup>> // 알람 그룹 목록 조회
    fun getTempAlarmGroups(): Flow<List<AlarmGroup>> // 임시 저장된 알람 그룹 목록 조회
    suspend fun createAlarmGroup(alarmGroup: AlarmGroup): Long
    suspend fun updateAlarmGroup(alarmGroup: AlarmGroup)
    suspend fun deleteAlarmGroup(alarmGroup: AlarmGroup)
    suspend fun deleteSelectedAlarmGroups(alarmGroups: List<AlarmGroup>)
}