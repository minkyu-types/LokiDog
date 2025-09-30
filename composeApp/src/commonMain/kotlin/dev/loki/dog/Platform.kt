package dev.loki.dog

import dev.loki.dog.di.storeFactoryModule
import dev.loki.dog.di.viewModelModule
import org.koin.core.module.Module

expect fun getAlarmDatabaseModule(): Module
expect fun getRepositoryModule(): Module
expect fun getNotificationManager()

fun getAppModules() = listOf(
    viewModelModule,
    storeFactoryModule,
    getAlarmDatabaseModule(),
    getRepositoryModule()
)