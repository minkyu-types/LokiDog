package dev.loki.alarm_data.repository

import dev.loki.alarm_data.dao.AlarmGroupDao
import dev.loki.alarm_data.mapper.AlarmGroupMapper
import dev.loki.alarm_data.mapper.AlarmMapper
import dev.loki.alarmgroup.model.AlarmGroup
import dev.loki.alarmgroup.model.AlarmGroupWithAlarms
import dev.loki.alarmgroup.model.AlarmMainSort
import dev.loki.alarmgroup.repository.AlarmGroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class AlarmGroupRepositoryImpl(
    private val alarmGroupDao: AlarmGroupDao,
    private val alarmMapper: AlarmMapper,
    private val alarmGroupMapper: AlarmGroupMapper,
) : AlarmGroupRepository {
    override fun getAlarmGroupWithAlarms(id: Long): Flow<AlarmGroupWithAlarms> {
        return alarmGroupDao.getAlarmGroupWithAlarms(id).map {
            AlarmGroupWithAlarms(
                group = alarmGroupMapper.mapToDomain(it.group),
                alarms = it.alarms.map { alarm ->
                    alarmMapper.mapToDomain(alarm)
                }
            )
        }
    }

    override fun getAlarmGroups(sort: AlarmMainSort): Flow<List<AlarmGroup>> {
        return when (sort) {
            AlarmMainSort.MOST_RECENT_CREATED -> alarmGroupDao.getAlarmGroupsByCreated()
            AlarmMainSort.MOST_RECENT_UPDATED -> alarmGroupDao.getAlarmGroupsByUpdated()
            AlarmMainSort.ACTIVATED_FIRST -> alarmGroupDao.getAlarmGroupsByActivated()
            AlarmMainSort.ALPHABETICAL -> alarmGroupDao.getAlarmGroupsByAlphabet()
            AlarmMainSort.CUSTOM -> alarmGroupDao.getAlarmGroupsByOrder()
        }.map { groups ->
            groups.map { group ->
                alarmGroupMapper.mapToDomain(group)
            }
        }
    }

    override suspend fun createAlarmGroup(alarmGroup: AlarmGroup): Long {
        val currTime = Clock.System.now().toEpochMilliseconds()
        val data = alarmGroupMapper.mapToData(
            alarmGroup.copy(created = currTime, updated = currTime)
        )
        return alarmGroupDao.insert(data)
    }

    override suspend fun updateAlarmGroup(alarmGroup: AlarmGroup) {
        val currTime = Clock.System.now().toEpochMilliseconds()
        val data = alarmGroupMapper.mapToData(
            alarmGroup.copy(updated = currTime)
        )
        alarmGroupDao.update(data)
    }

    override suspend fun deleteAlarmGroup(alarmGroup: AlarmGroup) {
        val data = alarmGroupMapper.mapToData(alarmGroup)
        alarmGroupDao.delete(data)
    }

    override suspend fun deleteSelectedAlarmGroups(alarmGroups: List<AlarmGroup>) {
        val data = alarmGroups.map { group ->
            group.id
        }
        alarmGroupDao.deleteSelectedAlarmGroups(data)
    }

    override fun getTempAlarmGroups(): Flow<List<AlarmGroup>> {
        return alarmGroupDao.getTempAlarmGroups()
            .map { groups ->
                groups.map { group ->
                    alarmGroupMapper.mapToDomain(group)
                }
            }
    }
}