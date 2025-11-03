package dev.loki.alarm.repository

import dev.loki.alarm.model.Alarm

interface AlarmRepository {

    suspend fun getAlarmById(id: Long): Alarm?
    suspend fun getAlarmsByGroupId(groupId: Long): List<Alarm>
    suspend fun upsertAlarm(alarm: Alarm): Alarm
    suspend fun deleteAlarm(alarm: Alarm)
}