package dev.loki.alarm_data.repository

import com.russhwolf.settings.Settings
import dev.loki.auth.model.User
import dev.loki.auth.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl(
    private val settings: Settings
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)

    init {
        // Load saved user from settings on init
        val savedUser = getSavedUser()
        _currentUser.value = savedUser
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            // In a real app, you would verify the token with your backend
            // For now, we'll just parse the token payload
            // This is a placeholder - you should implement proper token verification
            val user = User(
                id = idToken.hashCode().toString(),
                email = "",
                displayName = null,
                photoUrl = null
            )

            saveUser(user)
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            clearUser()
            _currentUser.value = null
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): Flow<User?> = _currentUser.asStateFlow()

    override suspend fun isUserSignedIn(): Boolean {
        return _currentUser.value != null
    }

    private fun saveUser(user: User) {
        settings.putString(KEY_USER_ID, user.id)
        settings.putString(KEY_USER_EMAIL, user.email)
        user.displayName?.let { settings.putString(KEY_USER_DISPLAY_NAME, it) }
        user.photoUrl?.let { settings.putString(KEY_USER_PHOTO_URL, it) }
    }

    private fun getSavedUser(): User? {
        val userId = settings.getStringOrNull(KEY_USER_ID) ?: return null
        val userEmail = settings.getStringOrNull(KEY_USER_EMAIL) ?: return null

        return User(
            id = userId,
            email = userEmail,
            displayName = settings.getStringOrNull(KEY_USER_DISPLAY_NAME),
            photoUrl = settings.getStringOrNull(KEY_USER_PHOTO_URL)
        )
    }

    private fun clearUser() {
        settings.remove(KEY_USER_ID)
        settings.remove(KEY_USER_EMAIL)
        settings.remove(KEY_USER_DISPLAY_NAME)
        settings.remove(KEY_USER_PHOTO_URL)
    }

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_DISPLAY_NAME = "user_display_name"
        private const val KEY_USER_PHOTO_URL = "user_photo_url"
    }
}