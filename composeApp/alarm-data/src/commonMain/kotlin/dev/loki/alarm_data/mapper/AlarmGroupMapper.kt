package dev.loki.alarm_data.mapper

import dev.loki.alarm_data.model.AlarmGroupEntity
import dev.loki.alarmgroup.model.AlarmGroup

class AlarmGroupMapper {

    fun mapToDomain(alarmGroup: AlarmGroupEntity): AlarmGroup {
        return AlarmGroup (
            order = alarmGroup.order,
            id = alarmGroup.id,
            title = alarmGroup.title,
            description = alarmGroup.description,
            repeatDays = alarmGroup.repeatDays,
            isActivated = alarmGroup.isActivated,
            created = alarmGroup.createdAt,
            updated = alarmGroup.updatedAt,
            isTemp = alarmGroup.isTemp
        )
    }

    fun mapToData(alarmGroup: AlarmGroup): AlarmGroupEntity {
        return AlarmGroupEntity(
            order = alarmGroup.order,
            id = alarmGroup.id,
            title = alarmGroup.title,
            description = alarmGroup.description,
            repeatDays = alarmGroup.repeatDays,
            createdAt = alarmGroup.created,
            updatedAt = alarmGroup.updated,
            isActivated = alarmGroup.isActivated,
            isTemp = alarmGroup.isTemp
        )
    }
}