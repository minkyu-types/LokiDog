package dev.loki.dog.di

import dev.loki.dog.feature.main.AlarmMainStoreFactory
import org.koin.dsl.module

val storeFactoryModule = module {
    single<AlarmMainStoreFactory> { AlarmMainStoreFactory() }
}