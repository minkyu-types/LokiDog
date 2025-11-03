package dev.loki.alarm_data.repository

import dev.loki.alarm.model.Alarm
import dev.loki.alarm.repository.AlarmRepository
import dev.loki.alarm_data.dao.AlarmDao
import dev.loki.alarm_data.dao.AlarmGroupDao
import dev.loki.alarm_data.mapper.AlarmMapper

class AlarmRepositoryImpl(
    private val alarmDao: AlarmDao,
    private val alarmGroupDao: AlarmGroupDao,
    private val alarmMapper: AlarmMapper
): AlarmRepository {
    override suspend fun getAlarmById(id: Long): Alarm? {
        return alarmDao.getAlarmById(id)?.let {
            alarmMapper.mapToDomain(it)
        }
    }

    override suspend fun getAlarmsByGroupId(groupId: Long): List<Alarm> {
        return alarmDao.getAlarmsByGroupId(groupId)
            .map {
                alarmMapper.mapToDomain(it)
            }
    }

    override suspend fun upsertAlarm(alarm: Alarm): Alarm {
        val data = alarmMapper.mapToData(alarm)
        val generatedId = alarmDao.upsert(data)
        val updatedAlarm = alarmMapper.mapToDomain(data).copy(id = generatedId)
        updateAlarmCount(alarm)

        return updatedAlarm
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        val data = alarmMapper.mapToData(alarm)
        alarmDao.delete(data)
        updateAlarmCount(alarm)
    }

    private suspend fun updateAlarmCount(alarm: Alarm) {
        val count = alarmDao.getAlarmCountByGroup(alarm.groupId)
        val group = alarmGroupDao.getAlarmGroupById(alarm.groupId)
        group?.copy(alarmSize = count)?.let {
            alarmGroupDao.update(it)
        }
    }
}