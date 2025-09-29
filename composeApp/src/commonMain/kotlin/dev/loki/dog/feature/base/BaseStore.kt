package dev.loki.dog.feature.base

import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinScopeComponent
import org.koin.core.scope.Scope
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.container

abstract class BaseStore<S: BaseState, SE: BaseSideEffect>(
    coroutineScope: CoroutineScope,
    override val scope: Scope,
    initialState: S,
): ContainerHost<S, SE>, KoinScopeComponent {

    override val container: Container<S, SE> = coroutineScope.container(initialState)
    protected val storeScope = coroutineScope

    abstract fun dispatch(action: BaseAction)

    protected inline fun setState(crossinline reducer: S.() -> S) {
        intent {
            reduce { state.reducer() }
        }
    }

    protected fun postEffect(effect: SE) {
        intent {
            postSideEffect(effect)
        }
    }
}