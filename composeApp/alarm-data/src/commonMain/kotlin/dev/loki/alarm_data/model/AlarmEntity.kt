package dev.loki.alarm_data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = AlarmGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 1,
    val groupId: Long?,
    val time: Long,
    val isActivated: Boolean,
)
