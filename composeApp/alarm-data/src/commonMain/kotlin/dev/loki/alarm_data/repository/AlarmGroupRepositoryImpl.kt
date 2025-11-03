package dev.loki.alarm_data.repository

import dev.loki.alarm_data.dao.AlarmGroupDao
import dev.loki.alarm_data.mapper.AlarmGroupMapper
import dev.loki.alarm_data.mapper.AlarmMapper
import dev.loki.alarmgroup.model.AlarmGroup
import dev.loki.alarmgroup.model.AlarmGroupWithAlarms
import dev.loki.alarmgroup.model.AlarmMainSort
import dev.loki.alarmgroup.repository.AlarmGroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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
        }.flowOn(Dispatchers.IO)
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
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun createAlarmGroup(alarmGroup: AlarmGroup): Long {
        val currTime = Clock.System.now().toEpochMilliseconds()
        val data = alarmGroupMapper.mapToData(
            alarmGroup.copy(created = currTime, updated = currTime)
        )
        return withContext(Dispatchers.IO) {
            alarmGroupDao.insert(data)
        }
    }

    override suspend fun updateAlarmGroup(alarmGroup: AlarmGroup) {
        val currTime = Clock.System.now().toEpochMilliseconds()
        val data = alarmGroupMapper.mapToData(
            alarmGroup.copy(updated = currTime)
        )
        withContext(Dispatchers.IO) {
            alarmGroupDao.update(data)
        }
    }

    override suspend fun deleteAlarmGroup(alarmGroup: AlarmGroup) {
        val data = alarmGroupMapper.mapToData(alarmGroup)
        withContext(Dispatchers.IO) {
            alarmGroupDao.delete(data)
        }
    }

    override suspend fun deleteSelectedAlarmGroups(alarmGroups: List<AlarmGroup>) {
        val data = alarmGroups.map { group ->
            group.id
        }
        withContext(Dispatchers.IO) {
            alarmGroupDao.deleteSelectedAlarmGroups(data)
        }
    }

    override fun getTempAlarmGroups(): Flow<List<AlarmGroup>> {
        return alarmGroupDao.getTempAlarmGroups()
            .map { groups ->
                groups.map { group ->
                    alarmGroupMapper.mapToDomain(group)
                }
            }.flowOn(Dispatchers.IO)
    }

    override suspend fun getAlarmGroupById(id: Long): AlarmGroup? {
        val data = withContext(Dispatchers.IO) {
            alarmGroupDao.getAlarmGroupById(id)
        }
        val domain = data?.let {
            alarmGroupMapper.mapToDomain(it)
        }
        return domain
    }
}