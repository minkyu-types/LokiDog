package dev.loki.dog.feature.base

import kotlinx.coroutines.CoroutineScope
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.container

internal abstract class BaseStore<S: BaseState, SE: BaseSideEffect>(
    scope: CoroutineScope,
    initialState: S,
): ContainerHost<S, SE> {

    override val container: Container<S, SE> = scope.container<S, SE>(initialState)
    protected val storeScope = scope

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