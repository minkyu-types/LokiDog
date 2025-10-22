package dev.loki

import dev.loki.alarm.model.Alarm
import kotlinx.datetime.DayOfWeek

interface AlarmScheduler {
    suspend fun schedule(repeatDays: Set<DayOfWeek>, alarm: Alarm)
    suspend fun cancel(repeatDays: Set<DayOfWeek>, alarm: Alarm)
}