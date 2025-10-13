package dev.loki.alarm.model

data class Alarm(
    val id: Long,
    val groupId: Long?,
    val time: Long,
    val isActivated: Boolean,
    val isTemp: Boolean,
)
