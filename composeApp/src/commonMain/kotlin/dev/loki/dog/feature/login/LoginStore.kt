package dev.loki.dog.feature.login

import dev.loki.DomainResult
import dev.loki.auth.usecase.GetCurrentUserUseCase
import dev.loki.auth.usecase.SignInWithAppleUseCase
import dev.loki.auth.usecase.SignInWithGoogleUseCase
import dev.loki.auth.usecase.SignOutUseCase
import dev.loki.dog.expect.LoginManager
import dev.loki.dog.feature.base.BaseAction
import dev.loki.dog.feature.base.BaseStore
import dev.loki.dog.feature.base.LoadState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.inject
import org.koin.core.scope.Scope

class LoginStore(
    coroutineScope: CoroutineScope,
    scope: Scope,
) : BaseStore<LoginState, LoginSideEffect>(
    coroutineScope = coroutineScope,
    scope = scope,
    initialState = LoginState()
) {
    private val loginManager: LoginManager by inject()
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase by inject()
    private val signInWithAppleUseCase: SignInWithAppleUseCase by inject()
    private val signOutUseCase: SignOutUseCase by inject()
    private val getCurrentUserUseCase: GetCurrentUserUseCase by inject()

    init {
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        setState { copy(currentUser = result.data) }
                    }
                    is DomainResult.Error -> {
                        setState { copy(currentUser = null) }
                    }
                }
            }
        }
    }

    override fun dispatch(action: BaseAction) {
        when (action) {
            is LoginAction.SignInWithGoogle -> signInWithGoogle()
            is LoginAction.SignInWithApple -> signInWithApple()
            is LoginAction.SignOut -> signOut()
            is LoginAction.CheckAuthState -> checkAuthState()
        }
    }

    private fun signInWithGoogle() {
        setState { copy(isSigningIn = true, loadState = LoadState.Loading) }

        loginManager.requestGoogleLogin(
            onSuccess = { idToken ->
                viewModelScope.launch {
                    signInWithGoogleUseCase(idToken).collect { result ->
                        when (result) {
                            is DomainResult.Success -> {
                                setState {
                                    copy(
                                        currentUser = result.data,
                                        isSigningIn = false,
                                        loadState = LoadState.Success
                                    )
                                }
                                postEffect(LoginSideEffect.NavigateToMain)
                            }
                            is DomainResult.Error -> {
                                setState {
                                    copy(
                                        isSigningIn = false,
                                        loadState = LoadState.Error(
                                            result.throwable.message ?: "Sign in failed"
                                        )
                                    )
                                }
                                postEffect(
                                    LoginSideEffect.ShowError(
                                        result.throwable.message ?: "Sign in failed"
                                    )
                                )
                            }
                        }
                    }
                }
            },
            onFailure = { errorMessage ->
                setState {
                    copy(
                        isSigningIn = false,
                        loadState = LoadState.Error(errorMessage)
                    )
                }
                postEffect(LoginSideEffect.ShowError(errorMessage))
            }
        )
    }

    private fun signInWithApple() {
        setState { copy(isSigningIn = true, loadState = LoadState.Loading) }

        loginManager.requestAppleLogin(
            onSuccess = { idToken ->
                viewModelScope.launch {
                    signInWithAppleUseCase(idToken).collect { result ->
                        when (result) {
                            is DomainResult.Success -> {
                                setState {
                                    copy(
                                        currentUser = result.data,
                                        isSigningIn = false,
                                        loadState = LoadState.Success
                                    )
                                }
                                postEffect(LoginSideEffect.NavigateToMain)
                            }
                            is DomainResult.Error -> {
                                setState {
                                    copy(
                                        isSigningIn = false,
                                        loadState = LoadState.Error(
                                            result.throwable.message ?: "Sign in failed"
                                        )
                                    )
                                }
                                postEffect(
                                    LoginSideEffect.ShowError(
                                        result.throwable.message ?: "Sign in failed"
                                    )
                                )
                            }
                        }
                    }
                }
            },
            onFailure = { errorMessage ->
                setState {
                    copy(
                        isSigningIn = false,
                        loadState = LoadState.Error(errorMessage)
                    )
                }
                postEffect(LoginSideEffect.ShowError(errorMessage))
            }
        )
    }

    private fun signOut() {
        viewModelScope.launch {
            setState { copy(loadState = LoadState.Loading) }

            signOutUseCase().collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        setState {
                            copy(
                                currentUser = null,
                                loadState = LoadState.Success
                            )
                        }
                    }
                    is DomainResult.Error -> {
                        setState {
                            copy(
                                loadState = LoadState.Error(
                                    result.throwable.message ?: "Sign out failed"
                                )
                            )
                        }
                        postEffect(
                            LoginSideEffect.ShowError(
                                result.throwable.message ?: "Sign out failed"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun checkAuthState() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        if (result.data != null) {
                            postEffect(LoginSideEffect.NavigateToMain)
                        }
                    }
                    is DomainResult.Error -> {
                        // Do nothing
                    }
                }
            }
        }
    }
}