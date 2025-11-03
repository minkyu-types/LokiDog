package dev.loki.alarm

import dev.loki.alarm.model.Alarm
import dev.loki.alarmgroup.model.AlarmGroup


fun generateAlarmGroup(id: Long, isActivate: Boolean = true, isTemp: Boolean = false): AlarmGroup {
    return AlarmGroup(
        id = id,
        order = 0,
        title = "Test Group",
        description = "This is a test group",
        includedAlarmsSize = 5,
        repeatDays = emptySet(),
        created = 1623456789L,
        updated = 1623456789L,
        isActivated = isActivate,
        isTemp = isTemp,
    )
}

fun generateAlarm(id: Long, groupId: Long, isActivated: Boolean = true, isTemp: Boolean = true): Alarm {
    return Alarm(
        id = id,
        groupId = groupId,
        time = "09:00",
        memo = "Morning Alarm",
        isActivated = isActivated,
        isTemp = isTemp
    )
}