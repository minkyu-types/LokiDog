package dev.loki.alarm_data.mapper

import dev.loki.alarm_data.model.AlarmGroupEntity
import dev.loki.alarmgroup.model.AlarmGroup

class AlarmGroupMapper {

    fun mapToDomain(alarm: AlarmGroupEntity): AlarmGroup {
        return AlarmGroup (
            id = alarm.id,
            alarms = alarm.alarms,
            title = alarm.title,
            description = alarm.description,
            isActivated = alarm.isActivated,
            created = alarm.created,
            updated = alarm.updated,
        )
    }

    fun mapToData(alarm: AlarmGroup): AlarmGroupEntity {
        return AlarmGroupEntity(
            id = alarm.id,
            alarms = alarm.alarms,
            title = alarm.title,
            description = alarm.description,
            isActivated = alarm.isActivated,
            created = alarm.created,
            updated = alarm.updated,
        )
    }
}