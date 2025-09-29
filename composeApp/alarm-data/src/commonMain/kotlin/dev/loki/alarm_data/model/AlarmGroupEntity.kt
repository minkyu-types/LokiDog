package dev.loki.alarm_data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.loki.alarm.model.Alarm

@Entity
data class AlarmGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 1,
    val alarms: List<Alarm>,
    val title: String,
    val description: String,
    val isActivated: Boolean,
    val created: Long,
    val updated: Long,
)
