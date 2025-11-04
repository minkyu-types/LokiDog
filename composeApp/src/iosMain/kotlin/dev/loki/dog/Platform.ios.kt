package dev.loki.dog

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import dev.loki.AlarmScheduler
import dev.loki.alarm_data.database.AlarmDatabase
import dev.loki.alarm_data.database.getAlarmDatabase
import dev.loki.alarm_data.di.alarmMapperModule
import dev.loki.alarm_data.di.alarmRepositoryModule
import dev.loki.alarm_data.expect.getAlarmDatabaseBuilder
import dev.loki.dog.expect.LoginManager
import dev.loki.dog.expect.PlatformAlarmScheduler
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual fun getAlarmDatabaseModule(): Module {
    return module {
        single {
            getAlarmDatabase(
                getAlarmDatabaseBuilder()
            )
        }
        single { get<AlarmDatabase>().getAlarmDao() }
        single { get<AlarmDatabase>().getAlarmGroupDao() }
        single { get<AlarmDatabase>().getTimerHistoryDao() }
    }
}

actual fun getRepositoryModule(): List<Module> = listOf(
    module {
        single<Settings> {
            NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
        }
    }
) + alarmRepositoryModule + alarmMapperModule

actual fun getAlarmScheduler(): Module {
    return module {
        single<AlarmScheduler> { PlatformAlarmScheduler() }
    }
}

actual fun getLoginManagerModule(): Module {
    return module {
        single { LoginManager() }
    }
}