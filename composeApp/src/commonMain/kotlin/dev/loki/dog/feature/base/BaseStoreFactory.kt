package dev.loki.dog.feature.base

import kotlinx.coroutines.CoroutineScope
import org.koin.core.scope.Scope

abstract class BaseStoreFactory<S: BaseState, SE: BaseSideEffect> {
    abstract fun create(coroutineScope: CoroutineScope, scope: Scope): BaseStore<S, SE>
}