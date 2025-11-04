package dev.loki.auth.model

data class User(
    val id: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?
)