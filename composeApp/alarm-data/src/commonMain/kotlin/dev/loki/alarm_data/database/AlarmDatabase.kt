package dev.loki.alarm_data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.loki.alarm_data.converter.DayOfWeekSetConverter
import dev.loki.alarm_data.dao.AlarmDao
import dev.loki.alarm_data.dao.AlarmGroupDao
import dev.loki.alarm_data.dao.TimerHistoryDao
import dev.loki.alarm_data.AlarmDatabaseConstructor
import dev.loki.alarm_data.model.AlarmEntity
import dev.loki.alarm_data.model.AlarmGroupEntity
import dev.loki.alarm_data.model.TimerHistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities =
        [
            AlarmEntity::class,
            AlarmGroupEntity::class,
            TimerHistoryEntity::class,
        ],
    version = 8
)
@TypeConverters(DayOfWeekSetConverter::class)
@ConstructedBy(AlarmDatabaseConstructor::class)
abstract class AlarmDatabase: RoomDatabase() {
    abstract fun getAlarmDao(): AlarmDao
    abstract fun getAlarmGroupDao(): AlarmGroupDao
    abstract fun getTimerHistoryDao(): TimerHistoryDao
}

fun getAlarmDatabase(
    builder: RoomDatabase.Builder<AlarmDatabase>
): AlarmDatabase {
    return builder
        .setQueryCoroutineContext(Dispatchers.IO)
        .fallbackToDestructiveMigration(false)
        .build()
}