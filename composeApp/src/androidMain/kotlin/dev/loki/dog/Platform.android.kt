package dev.loki.dog

import dev.loki.alarm_data.database.AlarmDatabase
import dev.loki.alarm_data.database.getAlarmDatabase
import dev.loki.alarm_data.di.alarmMapperModule
import dev.loki.alarm_data.di.alarmRepositoryModule
import dev.loki.alarm_data.expect.getAlarmDatabaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun getAlarmDatabaseModule(): Module {
    return module {
        single {
            getAlarmDatabase(
                getAlarmDatabaseBuilder(
                    androidContext()
                )
            )
        }
        single { get<AlarmDatabase>().getAlarmDao() }
        single { get<AlarmDatabase>().getAlarmGroupDao() }
    }
}

actual fun getRepositoryModule(): List<Module> = alarmRepositoryModule + alarmMapperModule