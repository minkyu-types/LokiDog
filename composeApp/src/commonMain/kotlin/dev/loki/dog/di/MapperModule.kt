package dev.loki.dog.di

import dev.loki.dog.mapper.AlarmGroupMapper
import dev.loki.dog.mapper.AlarmMapper
import dev.loki.dog.mapper.TimerHistoryMapper
import org.koin.dsl.module

val mapperModule = module {
    single<AlarmMapper> { AlarmMapper() }
    single<AlarmGroupMapper> { AlarmGroupMapper() }
    single<TimerHistoryMapper> { TimerHistoryMapper() }
}