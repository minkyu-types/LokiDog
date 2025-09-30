package dev.loki.alarm_data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlarmGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 1,
    val title: String,
    val alarms: List<AlarmEntity> = emptyList(),
    val description: String,
    val created: Long,
    val updated: Long,
    val isActivated: Boolean,
    val isTemp : Boolean,
)
