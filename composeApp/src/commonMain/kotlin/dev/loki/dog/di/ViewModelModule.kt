package dev.loki.dog.di

import dev.loki.dog.feature.addalarmgroup.AddAlarmGroupViewModel
import dev.loki.dog.feature.main.AlarmMainViewModel
import dev.loki.dog.feature.temp.TempAlarmGroupsViewModel
import org.koin.dsl.module

val viewModelModule = module {
    single<AlarmMainViewModel> { AlarmMainViewModel(get()) }
    single<TempAlarmGroupsViewModel> { TempAlarmGroupsViewModel(get()) }
    single<AddAlarmGroupViewModel> { AddAlarmGroupViewModel(get()) }
}