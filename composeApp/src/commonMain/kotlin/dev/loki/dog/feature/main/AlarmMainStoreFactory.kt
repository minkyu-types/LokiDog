package dev.loki.dog.feature.main

import dev.loki.dog.feature.base.BaseStore
import dev.loki.dog.feature.base.BaseStoreFactory
import kotlinx.coroutines.CoroutineScope
import org.koin.core.scope.Scope

class AlarmMainStoreFactory: BaseStoreFactory<AlarmMainState, AlarmMainSideEffect>() {
    override fun create(coroutineScope: CoroutineScope, scope: Scope): BaseStore<AlarmMainState, AlarmMainSideEffect> {
        return AlarmMainStore(
            coroutineScope = coroutineScope,
            scope = scope
        )
    }
}