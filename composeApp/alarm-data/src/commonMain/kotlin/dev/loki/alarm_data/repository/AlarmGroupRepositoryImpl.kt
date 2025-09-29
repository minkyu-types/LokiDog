package dev.loki.alarm_data.repository

import dev.loki.alarm_data.dao.AlarmGroupDao
import dev.loki.alarm_data.dao.TempAlarmGroupDao
import dev.loki.alarm_data.mapper.AlarmGroupMapper
import dev.loki.alarm_data.mapper.TempAlarmGroupMapper
import dev.loki.alarmgroup.model.AlarmGroup
import dev.loki.alarmgroup.model.AlarmMainSort
import dev.loki.alarmgroup.model.TempAlarmGroup
import dev.loki.alarmgroup.repository.AlarmGroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlarmGroupRepositoryImpl(
    private val alarmGroupDao: AlarmGroupDao,
    private val tempAlarmGroupDao: TempAlarmGroupDao,
    private val alarmGroupMapper: AlarmGroupMapper,
    private val tempAlarmGroupMapper: TempAlarmGroupMapper,
): AlarmGroupRepository {
    override fun getAlarmGroups(sort: AlarmMainSort): Flow<List<AlarmGroup>> {
        return when (sort) {
            AlarmMainSort.MOST_RECENT_CREATED -> alarmGroupDao.getAlarmGroupsByCreated(sort)
            AlarmMainSort.MOST_RECENT_UPDATED -> alarmGroupDao.getAlarmGroupsByUpdated(sort)
            AlarmMainSort.ACTIVATED_FIRST -> alarmGroupDao.getAlarmGroupsByActivated(sort)
        }.map { groups ->
            groups.map { group ->
                alarmGroupMapper.mapToDomain(group)
            }
        }
    }

    override suspend fun createAlarmGroup(alarmGroup: AlarmGroup) {
        val data =alarmGroupMapper.mapToData(alarmGroup)
        alarmGroupDao.insert(data)
    }

    override suspend fun updateAlarmGroup(alarmGroup: AlarmGroup) {
        val data =alarmGroupMapper.mapToData(alarmGroup)
        alarmGroupDao.update(data)
    }

    override suspend fun deleteAlarmGroup(alarmGroup: AlarmGroup) {
        val data =alarmGroupMapper.mapToData(alarmGroup)
        alarmGroupDao.delete(data)
    }

    override suspend fun deleteSelectedAlarmGroups(alarmGroups: List<AlarmGroup>) {
        val data = alarmGroups.map { group ->
            alarmGroupMapper.mapToData(group)
        }
        alarmGroupDao.deleteSelectedItems(data)
    }

    override fun getTempAlarmGroups(): Flow<List<TempAlarmGroup>> {
        TODO("Not yet implemented")
    }

    override suspend fun createTempAlarmGroup(tempAlarmGroup: TempAlarmGroup) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTempAlarmGroup(tempAlarmGroup: TempAlarmGroup) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTempAlarmGroup(tempAlarmGroup: TempAlarmGroup) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSelectedTempAlarmGroups(tempAlarmGroups: List<TempAlarmGroup>) {
        TODO("Not yet implemented")
    }
}