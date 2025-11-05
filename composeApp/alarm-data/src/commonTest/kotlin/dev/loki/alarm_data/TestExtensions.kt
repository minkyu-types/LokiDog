package dev.loki.alarm_data

import dev.loki.alarm_data.model.AlarmEntity
import dev.loki.alarm_data.model.AlarmGroupEntity

fun generateAlarmGroup(id: Long, isActivate: Boolean = true, isTemp: Boolean = false): AlarmGroupEntity {
    return AlarmGroupEntity(
        id = id,
        order = 0,
        title = "Test Group",
        description = "This is a test group",
        alarmSize = 5,
        repeatDays = emptySet(),
        createdAt = 1623456789L,
        updatedAt = 1623456789L,
        isActivated = isActivate,
        isTemp = isTemp,
    )
}

fun generateAlarm(id: Long, groupId: Long, isActivated: Boolean = true, isTemp: Boolean = true): AlarmEntity {
    return AlarmEntity(
        id = id,
        groupId = groupId,
        time = "09:00",
        memo = "Morning Alarm",
        isActivated = isActivated,
        isTemp = isTemp
    )
}