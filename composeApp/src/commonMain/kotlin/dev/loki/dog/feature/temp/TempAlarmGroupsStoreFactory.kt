package dev.loki.dog.feature.temp

import dev.loki.dog.feature.base.BaseStore
import dev.loki.dog.feature.base.BaseStoreFactory
import kotlinx.coroutines.CoroutineScope
import org.koin.core.scope.Scope

class TempAlarmGroupsStoreFactory: BaseStoreFactory<TempAlarmGroupsState, TempAlarmGroupsSideEffect>() {
    override fun create(
        coroutineScope: CoroutineScope,
        scope: Scope
    ): BaseStore<TempAlarmGroupsState, TempAlarmGroupsSideEffect> {
        return TempAlarmGroupsStore(
            coroutineScope = coroutineScope,
            scope = scope
        )
    }
}