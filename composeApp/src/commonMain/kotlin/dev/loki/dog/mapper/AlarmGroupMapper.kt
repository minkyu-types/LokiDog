package dev.loki.dog.mapper

import dev.loki.alarmgroup.model.AlarmGroup
import dev.loki.dog.model.AlarmGroupModel

class AlarmGroupMapper {

    fun mapToDomain(alarmGroup: AlarmGroupModel): AlarmGroup {
        return AlarmGroup (
            order = alarmGroup.order,
            id = alarmGroup.id,
            title = alarmGroup.title,
            description = alarmGroup.description,
            repeatDays = alarmGroup.repeatDays,
            isActivated = alarmGroup.isActivated,
            created = alarmGroup.created,
            updated = alarmGroup.updated,
            isTemp = alarmGroup.isTemp
        )
    }

    fun mapToPresentation(alarmGroup: AlarmGroup): AlarmGroupModel {
        return AlarmGroupModel(
            order = alarmGroup.order,
            id = alarmGroup.id,
            title = alarmGroup.title,
            repeatDays = alarmGroup.repeatDays,
            description = alarmGroup.description,
            created = alarmGroup.created,
            updated = alarmGroup.updated,
            isActivated = alarmGroup.isActivated,
            isTemp = alarmGroup.isTemp
        )
    }
}