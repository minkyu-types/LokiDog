package dev.loki.dog.feature.login

import dev.loki.dog.feature.base.BaseStore
import dev.loki.dog.feature.base.BaseStoreFactory
import kotlinx.coroutines.CoroutineScope
import org.koin.core.scope.Scope

class LoginStoreFactory(
): BaseStoreFactory<LoginState, LoginSideEffect>() {
    override fun create(coroutineScope: CoroutineScope, scope: Scope): BaseStore<LoginState, LoginSideEffect> {
        return LoginStore(
            coroutineScope = coroutineScope,
            scope = scope,
        )
    }
}