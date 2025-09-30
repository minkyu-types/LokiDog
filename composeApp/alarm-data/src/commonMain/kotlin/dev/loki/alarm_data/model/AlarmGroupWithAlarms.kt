package dev.loki.alarm_data.model

import androidx.room.Embedded
import androidx.room.Relation

data class AlarmGroupWithAlarms(
    @Embedded val group: AlarmGroupEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "groupId"
    )
    val alarms: List<AlarmEntity>
)