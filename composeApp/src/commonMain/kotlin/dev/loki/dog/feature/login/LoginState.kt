package dev.loki.dog.feature.login

import dev.loki.auth.model.User
import dev.loki.dog.feature.base.BaseState
import dev.loki.dog.feature.base.LoadState

data class LoginState(
    override val loadState: LoadState = LoadState.Idle,
    val currentUser: User? = null,
    val isSigningIn: Boolean = false
): BaseState