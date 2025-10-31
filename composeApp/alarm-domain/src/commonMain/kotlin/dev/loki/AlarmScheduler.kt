package dev.loki

import dev.loki.alarm.model.Alarm
import kotlinx.datetime.DayOfWeek

interface AlarmScheduler {
    suspend fun schedule(repeatDays: Set<DayOfWeek>, alarm: Alarm)
    fun cancel(repeatDays: Set<DayOfWeek>, alarm: Alarm)
    fun cancelByGroup(groupId: Long)
    fun scheduleTimer(triggerTime: Long)
    fun cancelTimer()
}