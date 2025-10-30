package dev.loki.dog.di

import dev.loki.dog.feature.addalarmgroup.AddAlarmGroupStoreFactory
import dev.loki.dog.feature.main.AlarmMainStoreFactory
import dev.loki.dog.feature.temp.TempAlarmGroupsStoreFactory
import dev.loki.dog.feature.timer.TimerStoreFactory
import org.koin.dsl.module

val storeFactoryModule = module {
    single<AlarmMainStoreFactory> { AlarmMainStoreFactory() }
    single<TempAlarmGroupsStoreFactory> { TempAlarmGroupsStoreFactory() }
    single<AddAlarmGroupStoreFactory> { AddAlarmGroupStoreFactory() }
    single<TimerStoreFactory> { TimerStoreFactory() }
}