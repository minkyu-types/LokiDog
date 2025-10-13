package dev.loki.alarmgroup.model

enum class AlarmMainSort(val label: String) {
    MOST_RECENT_CREATED("최신 등록순"),
    MOST_RECENT_UPDATED("최신 업데이트순"),
    ACTIVATED_FIRST("활성화순"),
    ALPHABETICAL("알파벳순")
}