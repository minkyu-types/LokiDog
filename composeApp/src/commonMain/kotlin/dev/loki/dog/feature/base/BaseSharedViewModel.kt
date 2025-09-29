package dev.loki.dog.feature.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.scope.Scope

abstract class BaseSharedViewModel<S: BaseState, SE: BaseSideEffect>(
    factory: BaseStoreFactory<S, SE>
): ViewModel(), KoinScopeComponent {

    final override val scope: Scope by lazy { createScope(this) }
    private val store: BaseStore<S, SE> = factory.create(viewModelScope, scope)

    val state: StateFlow<S> = store.container.stateFlow
    val effect: Flow<SE> = store.container.sideEffectFlow

    fun dispatch(action: BaseAction) {
        store.dispatch(action)
    }

    override fun onCleared() {
        super.onCleared()
        scope.close()
    }
}