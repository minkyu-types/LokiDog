package dev.loki.alarmgroup.model

import dev.loki.alarm.model.Alarm

data class AlarmGroup(
    val id: Long,
    val title: String,
    val description: String,
    val isActivated: Boolean,
    val created: Long,
    val updated: Long,
    val isTemp : Boolean,
)
