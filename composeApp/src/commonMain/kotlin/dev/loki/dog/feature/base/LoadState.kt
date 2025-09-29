package dev.loki.dog.feature.base

sealed interface LoadState {
    data object Idle: LoadState
    data object Loading: LoadState
    data object Success: LoadState
    data class Error(val message: String): LoadState
}