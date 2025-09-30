package dev.loki.dog.feature

enum class MainScreens(val title: String) {
    ALARM_MAIN("알람/그룹 목록"),
    TIMER_MAIN("타이머")
}

enum class SubScreens(val title: String) {
    ALARM_GROUP_DETAIL("알람 그룹 상세"),
    TEMP_ALARM_GROUP_LIST("임시저장 알람 그룹 목록"),
}