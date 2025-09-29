package dev.loki.alarmgroup.model

import dev.loki.alarm.model.Alarm

data class TempAlarmGroup(
    val id: Long = 0,
    val alarms: List<Alarm> = emptyList(),
    val title: String,
    val description: String,
    val created: Long
)
