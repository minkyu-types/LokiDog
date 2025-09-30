package dev.loki.alarmgroup.model

import dev.loki.alarm.model.Alarm

data class AlarmGroupWithAlarms(
    val group: AlarmGroup,
    val alarms: List<Alarm>
)