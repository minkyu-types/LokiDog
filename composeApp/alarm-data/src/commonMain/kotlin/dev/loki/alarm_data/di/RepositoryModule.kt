package dev.loki.alarm_data.di

import dev.loki.alarm.repository.AlarmRepository
import dev.loki.alarm_data.repository.AlarmGroupRepositoryImpl
import dev.loki.alarm_data.repository.AlarmRepositoryImpl
import dev.loki.alarmgroup.repository.AlarmGroupRepository
import org.koin.dsl.module

val alarmRepositoryModule = module {
    single<AlarmGroupRepository> { AlarmGroupRepositoryImpl(get(), get(), get()) }
    single<AlarmRepository> { AlarmRepositoryImpl(get(), get()) }
}