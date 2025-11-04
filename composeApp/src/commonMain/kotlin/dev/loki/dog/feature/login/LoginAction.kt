package dev.loki.dog.feature.login

import dev.loki.dog.feature.base.BaseAction

sealed class LoginAction: BaseAction {
    data object SignInWithGoogle: LoginAction()
    data object SignInWithApple: LoginAction()
    data object SignOut: LoginAction()
    data object CheckAuthState: LoginAction()
}