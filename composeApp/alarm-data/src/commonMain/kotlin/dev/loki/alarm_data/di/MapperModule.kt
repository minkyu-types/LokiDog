package dev.loki.alarm_data.di

import dev.loki.alarm_data.mapper.AlarmGroupMapper
import dev.loki.alarm_data.mapper.AlarmMapper
import dev.loki.alarm_data.mapper.TimerHistoryMapper
import org.koin.dsl.module

val alarmMapperModule = module {
    single<AlarmGroupMapper> { AlarmGroupMapper() }
    single<AlarmMapper> { AlarmMapper() }
    single<TimerHistoryMapper> { TimerHistoryMapper() }
}