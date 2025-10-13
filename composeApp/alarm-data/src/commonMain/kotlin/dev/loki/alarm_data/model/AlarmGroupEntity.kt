package dev.loki.alarm_data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_group")
data class AlarmGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isActivated: Boolean,
    val isTemp : Boolean,
)
