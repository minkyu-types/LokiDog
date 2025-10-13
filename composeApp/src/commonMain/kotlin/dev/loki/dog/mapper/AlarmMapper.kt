package dev.loki.dog.mapper

import dev.loki.alarm.model.Alarm
import dev.loki.dog.model.AlarmModel

class AlarmMapper {

    fun mapToDomain(alarm: AlarmModel): Alarm {
        return Alarm (
            id = alarm.id,
            groupId = alarm.groupId,
            time = alarm.time,
            isActivated = alarm.isActivated,
            isTemp = alarm.isTemp
        )
    }

    fun mapToPresentation(alarm: Alarm): AlarmModel {
        return AlarmModel(
            id = alarm.id,
            groupId = alarm.groupId,
            time = alarm.time,
            isActivated = alarm.isActivated,
            isTemp = alarm.isTemp
        )
    }
}