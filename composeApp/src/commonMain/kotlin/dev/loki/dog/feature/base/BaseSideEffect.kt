package dev.loki.dog.feature.base

interface BaseSideEffect {
    data class ShowAlert(val message: String): BaseSideEffect
}