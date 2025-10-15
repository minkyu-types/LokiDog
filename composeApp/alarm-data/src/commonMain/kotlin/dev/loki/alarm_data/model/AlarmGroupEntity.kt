package dev.loki.alarm_data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.DayOfWeek

@Entity(tableName = "alarm_group")
data class AlarmGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val order: Int,
    val title: String,
    val description: String,
    val repeatDays: Set<DayOfWeek> = emptySet(),
    val createdAt: Long,
    val updatedAt: Long,
    val isActivated: Boolean,
    val isTemp : Boolean,
)
