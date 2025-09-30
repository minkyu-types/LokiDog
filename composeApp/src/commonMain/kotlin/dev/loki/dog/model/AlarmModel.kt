package dev.loki.dog.model

data class AlarmModel(
    val id: Long,
    val groupId: Long?,
    val time: Long,
    val isActivated: Boolean,
)
