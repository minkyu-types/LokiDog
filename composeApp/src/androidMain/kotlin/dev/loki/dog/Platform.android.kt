package dev.loki.dog

import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import dev.loki.AlarmScheduler
import dev.loki.alarm_data.database.AlarmDatabase
import dev.loki.alarm_data.database.getAlarmDatabase
import dev.loki.alarm_data.di.alarmMapperModule
import dev.loki.alarm_data.di.alarmRepositoryModule
import dev.loki.alarm_data.getAlarmDatabaseBuilder
import dev.loki.dog.expect.LoginManager
import dev.loki.dog.expect.PlatformAlarmScheduler
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
        single { get<AlarmDatabase>().getTimerHistoryDao() }
    }
}

actual fun getRepositoryModule(): List<Module> = listOf(
    module {
        single<Settings> {
            SharedPreferencesSettings(
                androidContext().getSharedPreferences("loki_dog_prefs", android.content.Context.MODE_PRIVATE)
            )
        }
    }
) + alarmRepositoryModule + alarmMapperModule

actual fun getAlarmScheduler(): Module {
    return module {
        single<AlarmScheduler> { PlatformAlarmScheduler(androidContext()) }
    }
}

actual fun getLoginManagerModule(): Module {
    return module {
        single { LoginManager(androidContext()) }
    }
}