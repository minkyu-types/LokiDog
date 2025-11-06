package dev.loki.auth.usecase

import dev.loki.DomainResult
import dev.loki.auth.model.User
import dev.loki.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SignInWithAppleUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(idToken: String): Flow<DomainResult<User>> = flow {
        try {
            val result = authRepository.signInWithApple(idToken)
            result.onSuccess { user ->
                emit(DomainResult.Success(user))
            }.onFailure { throwable ->
                emit(DomainResult.Error(throwable))
            }
        } catch (e: Exception) {
            emit(DomainResult.Error(e))
        }
    }
}