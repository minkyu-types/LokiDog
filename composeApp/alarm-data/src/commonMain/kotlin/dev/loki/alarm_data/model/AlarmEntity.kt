package dev.loki.alarm_data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 1,
    val groupId: Long?,
    val time: Long,
    val isActivated: Boolean,
)
