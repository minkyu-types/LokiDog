package dev.loki.dog.mapper

import dev.loki.alarmgroup.model.AlarmGroup
import dev.loki.dog.model.AlarmGroupModel

class AlarmGroupMapper {

    fun mapToDomain(alarmGroup: AlarmGroupModel): AlarmGroup {
        return AlarmGroup (
            id = alarmGroup.id,
            title = alarmGroup.title,
            description = alarmGroup.description,
            isActivated = alarmGroup.isActivated,
            created = alarmGroup.created,
            updated = alarmGroup.updated,
            isTemp = alarmGroup.isTemp
        )
    }

    fun mapToPresentation(alarmGroup: AlarmGroup): AlarmGroupModel {
        return AlarmGroupModel(
            id = alarmGroup.id,
            title = alarmGroup.title,
            repeatDays = emptySet(),
            description = alarmGroup.description,
            created = alarmGroup.created,
            updated = alarmGroup.updated,
            isActivated = alarmGroup.isActivated,
            isTemp = alarmGroup.isTemp
        )
    }
}