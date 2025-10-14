package dev.loki.alarmgroup.model

data class AlarmGroup(
    val order: Int,
    val id: Long,
    val title: String,
    val description: String,
    val isActivated: Boolean,
    val created: Long,
    val updated: Long,
    val isTemp : Boolean,
)
