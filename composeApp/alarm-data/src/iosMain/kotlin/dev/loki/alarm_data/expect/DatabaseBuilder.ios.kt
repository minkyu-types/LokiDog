package dev.loki.alarm_data.expect

import androidx.room.Room
import androidx.room.RoomDatabase
import dev.loki.alarm_data.database.AlarmDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getAlarmDatabaseBuilder(): RoomDatabase.Builder<AlarmDatabase> {
    val dbFilePath = documentDirectory() + "/alarm.db"
    return Room.databaseBuilder<AlarmDatabase>(
        name = dbFilePath,
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}