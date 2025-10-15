package dev.loki.dog.feature

sealed interface Screens

enum class MainScreen: Screens {
    ALARM,
    TIMER
}

enum class SubScreen: Screens {
    ALARM_GROUP_ADD,
    ALARM_GROUP_TEMP_LIST,
}