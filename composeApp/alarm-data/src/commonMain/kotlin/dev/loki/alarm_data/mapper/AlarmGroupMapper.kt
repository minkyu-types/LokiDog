package dev.loki.alarm_data.mapper

import dev.loki.alarm.model.Alarm
import dev.loki.alarm_data.model.AlarmEntity
import dev.loki.alarm_data.model.AlarmGroupEntity
import dev.loki.alarmgroup.model.AlarmGroup

class AlarmGroupMapper {

    fun mapToDomain(alarmGroup: AlarmGroupEntity): AlarmGroup {
        return AlarmGroup (
            id = alarmGroup.id,
            title = alarmGroup.title,
            alarms = alarmGroup.alarms.map { alarm ->
                Alarm(
                    id = alarm.id,
                    groupId = alarm.groupId,
                    time = alarm.time,
                    isActivated = alarm.isActivated
                )
            },
            description = alarmGroup.description,
            isActivated = alarmGroup.isActivated,
            created = alarmGroup.created,
            updated = alarmGroup.updated,
            isTemp = alarmGroup.isTemp
        )
    }

    fun mapToData(alarmGroup: AlarmGroup): AlarmGroupEntity {
        return AlarmGroupEntity(
            id = alarmGroup.id,
            title = alarmGroup.title,
            alarms = alarmGroup.alarms.map { alarm ->
                AlarmEntity(
                    id = alarm.id,
                    groupId = alarm.groupId,
                    time = alarm.time,
                    isActivated = alarm.isActivated
                )
            },
            description = alarmGroup.description,
            created = alarmGroup.created,
            updated = alarmGroup.updated,
            isActivated = alarmGroup.isActivated,
            isTemp = alarmGroup.isTemp
        )
    }
}