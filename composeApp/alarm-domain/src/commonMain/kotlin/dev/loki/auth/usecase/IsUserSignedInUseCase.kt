package dev.loki.auth.usecase

import dev.loki.DomainResult
import dev.loki.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class IsUserSignedInUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<DomainResult<Boolean>> = flow {
        try {
            val isSignedIn = authRepository.isUserSignedIn()
            emit(DomainResult.Success(isSignedIn))
        } catch (e: Exception) {
            emit(DomainResult.Error(e))
        }
    }
}