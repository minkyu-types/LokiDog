package dev.loki.alarm

import dev.loki.alarm.model.Alarm

interface AlarmRepository {

    suspend fun createAlarm(alarm: Alarm)
    suspend fun updateAlarm(alarm: Alarm)
    suspend fun deleteAlarm(alarm: Alarm)
}