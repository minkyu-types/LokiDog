package dev.loki.dog.feature

sealed interface Screens {
    val title: String
}

sealed class MainScreen(override val title: String): Screens {
    data object AlarmMain: MainScreen("알람/그룹 목록")
    data object TimerMain: MainScreen("타이머")
}

sealed class SubScreen(override val title: String): Screens {
    data object AlarmGroupAdd: SubScreen("알람 그룹 추가")
    data object AlarmGroupDetail: SubScreen("알람 그룹 상세")
    data object TempAlarmGroupList: SubScreen("임시 저장 알람 그룹 목록")
}