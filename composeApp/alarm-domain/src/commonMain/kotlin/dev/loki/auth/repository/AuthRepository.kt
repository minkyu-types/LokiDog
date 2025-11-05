package dev.loki.auth.repository

import dev.loki.auth.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signOut(): Result<Unit>
    fun getCurrentUser(): Flow<User?>
    suspend fun isUserSignedIn(): Boolean
}