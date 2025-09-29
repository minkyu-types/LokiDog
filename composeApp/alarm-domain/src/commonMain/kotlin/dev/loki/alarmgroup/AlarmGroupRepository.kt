package dev.loki.alarmgroup

import dev.loki.alarmgroup.model.AlarmGroup
import kotlinx.coroutines.flow.Flow

interface AlarmGroupRepository {

    fun getAlarmGroups(): Flow<List<AlarmGroup>> // 알람 그룹 목록 조회
    fun getTempAlarmGroups(): Flow<List<AlarmGroup>> // 임시 저장된 알람 그룹 목록 조회
    suspend fun createAlarmGroup(alarmGroup: AlarmGroup)
    suspend fun updateAlarmGroup(alarmGroup: AlarmGroup)
    suspend fun deleteAlarmGroup(alarmGroup: AlarmGroup)
}