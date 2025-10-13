package dev.loki.dog

import dev.loki.di.alarmUseCaseModule
import dev.loki.dog.di.mapperModule
import dev.loki.dog.di.storeFactoryModule
import dev.loki.dog.di.viewModelModule
import org.koin.core.module.Module

expect fun getAlarmDatabaseModule(): Module
expect fun getRepositoryModule(): List<Module>

fun getAppModules() = listOf(
    mapperModule,
    viewModelModule,
    storeFactoryModule,
    alarmUseCaseModule,
    getAlarmDatabaseModule(),
) + getRepositoryModule()