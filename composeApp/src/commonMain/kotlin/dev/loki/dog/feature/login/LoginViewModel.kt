package dev.loki.dog.feature.login

import dev.loki.dog.feature.base.BaseSharedViewModel

class LoginViewModel(
    factory: LoginStoreFactory
) : BaseSharedViewModel<LoginState, LoginSideEffect>(
    factory = factory
) {

    fun signInWithGoogle() {
        dispatch(LoginAction.SignInWithGoogle)
    }

    fun signInWithApple() {
        dispatch(LoginAction.SignInWithApple)
    }

    fun checkAuthState() {
        dispatch(LoginAction.CheckAuthState)
    }
}