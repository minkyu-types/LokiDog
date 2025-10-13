package dev.loki.dog.feature.main

import dev.loki.alarmgroup.model.AlarmMainSort
import dev.loki.dog.feature.base.BaseSideEffect

sealed class AlarmMainSideEffect: BaseSideEffect {
    data class ShowSortBottomSheet(val sort: AlarmMainSort): AlarmMainSideEffect()
}