package dev.loki.dog.feature.timer

import dev.loki.AlarmScheduler
import dev.loki.dog.feature.base.BaseStore
import dev.loki.dog.feature.base.BaseStoreFactory
import kotlinx.coroutines.CoroutineScope
import org.koin.core.scope.Scope
import org.koin.mp.KoinPlatform.getKoin

class TimerStoreFactory: BaseStoreFactory<TimerState, TimerSideEffect>() {
    override fun create(
        coroutineScope: CoroutineScope,
        scope: Scope
    ): BaseStore<TimerState, TimerSideEffect> {
        return TimerStore(
            coroutineScope = coroutineScope,
            scope = scope,
            alarmScheduler = getKoin().get<AlarmScheduler>(),
            timerHistoryMapper = getKoin().get(),
            getTimerHistoriesUseCase = getKoin().get(),
            createTimerHistoryUseCase = getKoin().get(),
            deleteTimerHistoryUseCase = getKoin().get(),
        )
    }
}