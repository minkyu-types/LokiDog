package dev.loki.dog

import org.koin.core.context.startKoin

fun doInitKoin() {
    startKoin {
        modules(
            getAppModules()
        )
    }
}