package dev.loki.auth.usecase

import dev.loki.DomainResult
import dev.loki.auth.model.User
import dev.loki.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<DomainResult<User?>> =
        authRepository.getCurrentUser().map { user ->
            DomainResult.Success(user)
        }
}