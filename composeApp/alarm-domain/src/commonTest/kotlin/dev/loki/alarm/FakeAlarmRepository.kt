package dev.loki.alarm

import dev.loki.alarm.model.Alarm
import dev.loki.alarm.repository.AlarmRepository

class FakeAlarmRepository : AlarmRepository {
    val upsertedAlarms = mutableListOf<Alarm>()
    private val alarms = mutableMapOf<Long, Alarm>()

    fun addAlarm(alarm: Alarm) {
        alarms[alarm.id] = alarm
    }

    override suspend fun getAlarmById(id: Long): Alarm? {
        return alarms[id]
    }

    override suspend fun getAlarmsByGroupId(groupId: Long): List<Alarm> {
        return alarms.values.filter { it.groupId == groupId }
    }

    override suspend fun upsertAlarm(alarm: Alarm): Alarm {
        upsertedAlarms.add(alarm)
        alarms[alarm.id] = alarm
        return alarm
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        alarms.remove(alarm.id)
    }
}