package dev.loki.alarm_data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.loki.alarm.model.Alarm

@Entity
data class TempAlarmGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 1,
    val alarms: List<Alarm> = emptyList(),
    val title: String,
    val description: String,
    val created: Long
)
