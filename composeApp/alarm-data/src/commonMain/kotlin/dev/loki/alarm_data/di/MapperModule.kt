package dev.loki.alarm_data.di

import dev.loki.alarm_data.mapper.AlarmGroupMapper
import dev.loki.alarm_data.mapper.AlarmMapper
import org.koin.dsl.module

val mapperModules = module {
    single<AlarmGroupMapper> { AlarmGroupMapper() }
    single<AlarmMapper> { AlarmMapper() }
}