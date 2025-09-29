package dev.loki.dog.feature.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.scope.Scope

abstract class BaseSharedViewModel: ViewModel(), KoinScopeComponent {

    override val scope: Scope by lazy { createScope(this) }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        scope.close()
    }
}