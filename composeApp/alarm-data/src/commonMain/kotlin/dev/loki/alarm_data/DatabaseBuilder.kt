package dev.loki.alarm_data

import androidx.room.RoomDatabaseConstructor
import dev.loki.alarm_data.database.AlarmDatabase

@Suppress("KotlinNoActualForExpect")
expect object AlarmDatabaseConstructor: RoomDatabaseConstructor<AlarmDatabase> {
    override fun initialize(): AlarmDatabase
}