package dev.loki.dog

import org.koin.core.module.Module

expect fun getAlarmDatabaseModule(): Module

fun getAppModules() = listOf(
    getAlarmDatabaseModule()
)