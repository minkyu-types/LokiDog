package dev.loki.dog.feature

sealed interface Screens

enum class AuthScreen: Screens {
    LOGIN
}

enum class MainScreen: Screens {
    ALARM,
    TIMER
}

enum class SubScreen: Screens {
    ALARM_GROUP_ADD,
    ALARM_GROUP_TEMP_LIST,
}