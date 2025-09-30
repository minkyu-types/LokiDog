package dev.loki.alarm_data.di

import dev.loki.alarm_data.dao.AlarmDao
import dev.loki.alarm_data.dao.AlarmGroupDao
import dev.loki.alarm_data.database.AlarmDatabase
import org.koin.dsl.module

val daoModules = module {
    single<AlarmDao> { get<AlarmDatabase>().getAlarmDao() }
    single<AlarmGroupDao> { get<AlarmDatabase>().getAlarmGroupDao() }
}