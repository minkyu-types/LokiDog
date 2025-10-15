package dev.loki.alarmgroup.model

import kotlinx.datetime.DayOfWeek

data class AlarmGroup(
    val order: Int,
    val id: Long,
    val title: String,
    val description: String,
    val repeatDays: Set<DayOfWeek> = emptySet(),
    val isActivated: Boolean,
    val created: Long,
    val updated: Long,
    val isTemp : Boolean,
)
