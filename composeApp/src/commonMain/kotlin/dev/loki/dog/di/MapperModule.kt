package dev.loki.dog.di

import dev.loki.dog.mapper.AlarmGroupMapper
import dev.loki.dog.mapper.AlarmMapper
import org.koin.dsl.module

val mapperModule = module {
    single<AlarmMapper> { AlarmMapper() }
    single<AlarmGroupMapper> { AlarmGroupMapper() }
}