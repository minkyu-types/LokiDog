package dev.loki.alarm_data.mapper

import dev.loki.alarm.model.Alarm
import dev.loki.alarm_data.model.AlarmEntity

class AlarmMapper {

    fun mapToDomain(alarm: AlarmEntity): Alarm {
        return Alarm (
            id = alarm.id,
            groupId = alarm.groupId,
            time = alarm.time,
            isActivated = alarm.isActivated,
        )
    }

    fun mapToData(alarm: Alarm): AlarmEntity {
        return AlarmEntity(
            id = alarm.id,
            groupId = alarm.groupId,
            time = alarm.time,
            isActivated = alarm.isActivated,
        )
    }
}