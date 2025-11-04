package dev.loki.auth.usecase

import dev.loki.DomainResult
import dev.loki.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SignOutUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<DomainResult<Unit>> = flow {
        try {
            val result = authRepository.signOut()
            result.onSuccess {
                emit(DomainResult.Success(Unit))
            }.onFailure { throwable ->
                emit(DomainResult.Error(throwable))
            }
        } catch (e: Exception) {
            emit(DomainResult.Error(e))
        }
    }
}