package dev.loki.dog.feature.timer

import dev.loki.dog.feature.base.BaseSideEffect

sealed class TimerSideEffect: BaseSideEffect {
    data object Finished: TimerSideEffect()
}