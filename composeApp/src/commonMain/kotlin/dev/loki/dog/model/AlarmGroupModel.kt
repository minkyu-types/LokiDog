package dev.loki.dog.model

data class AlarmGroupModel(
    val id: Long,
    val title: String,
    val alarms: List<AlarmModel> = emptyList(),
    val description: String,
    val created: Long,
    val updated: Long,
    val isActivated: Boolean,
    val isTemp : Boolean,
)