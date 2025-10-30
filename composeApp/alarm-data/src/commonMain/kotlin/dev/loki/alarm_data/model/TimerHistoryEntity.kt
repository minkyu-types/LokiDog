package dev.loki.alarm_data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "timerHistory",
    indices = [Index(value = ["id"])]
)
data class TimerHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val durationTimeMillis: Long,
)
