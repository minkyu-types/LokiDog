package dev.loki.dog.feature.login

import dev.loki.dog.feature.base.BaseSideEffect

sealed class LoginSideEffect: BaseSideEffect {
    data object NavigateToMain: LoginSideEffect()
    data class ShowError(val message: String): LoginSideEffect()
    data object ShowGoogleSignIn: LoginSideEffect()
}