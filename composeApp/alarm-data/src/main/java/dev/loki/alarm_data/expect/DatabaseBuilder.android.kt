package dev.loki.alarm_data.expect

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.loki.alarm_data.database.AlarmDatabase

fun getAlarmDatabaseBuilder(context: Context): RoomDatabase.Builder<AlarmDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("alarm.db")
    return Room.databaseBuilder<AlarmDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}