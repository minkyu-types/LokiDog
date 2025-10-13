package dev.loki.alarm_data.repository

import dev.loki.alarm.model.Alarm
import dev.loki.alarm.repository.AlarmRepository
import dev.loki.alarm_data.dao.AlarmDao
import dev.loki.alarm_data.mapper.AlarmMapper

class AlarmRepositoryImpl(
    private val alarmDao: AlarmDao,
    private val alarmMapper: AlarmMapper
): AlarmRepository {
    override suspend fun createAlarm(alarm: Alarm) {
        val data = alarmMapper.mapToData(alarm)
        alarmDao.insert(data)
    }

    override suspend fun upsertAlarm(alarm: Alarm) {
        val data = alarmMapper.mapToData(alarm)
        alarmDao.upsert(data)
    }

    override suspend fun deleteAlarm(alarm: Alarm) {
        val data = alarmMapper.mapToData(alarm)
        alarmDao.delete(data)
    }
}