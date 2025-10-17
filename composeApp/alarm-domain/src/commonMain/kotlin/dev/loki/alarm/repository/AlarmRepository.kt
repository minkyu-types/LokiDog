package dev.loki.alarm.repository

import dev.loki.alarm.model.Alarm

interface AlarmRepository {

    suspend fun upsertAlarm(alarm: Alarm)
    suspend fun deleteAlarm(alarm: Alarm)
}