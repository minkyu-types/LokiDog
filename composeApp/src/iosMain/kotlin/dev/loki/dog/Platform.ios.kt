package dev.loki.dog

import dev.loki.alarm_data.database.getAlarmDatabase
import dev.loki.alarm_data.di.alarmMapperModule
import dev.loki.alarm_data.di.alarmRepositoryModule
import dev.loki.alarm_data.expect.getAlarmDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun getAlarmDatabaseModule(): Module {
    return module {
        single {
            getAlarmDatabase(
                getAlarmDatabaseBuilder()
            )
        }
    }
}

actual fun getRepositoryModule(): List<Module> = alarmRepositoryModule + alarmMapperModule