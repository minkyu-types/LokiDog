package dev.loki.alarm

import dev.loki.alarm.model.Alarm
import dev.loki.alarmgroup.model.AlarmGroup
import dev.loki.alarmgroup.model.AlarmGroupWithAlarms
import dev.loki.alarmgroup.model.AlarmMainSort
import dev.loki.alarmgroup.repository.AlarmGroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeAlarmGroupRepository : AlarmGroupRepository {
    private val alarmGroups = mutableMapOf<Long, AlarmGroup>()
    private val alarms = mutableMapOf<Long, MutableList<Alarm>>()
    private var nextId = 1L

    private val alarmGroupsFlow = MutableStateFlow<List<AlarmGroup>>(emptyList())
    private val alarmsFlow = MutableStateFlow<Map<Long, List<Alarm>>>(emptyMap())

    // 테스트용 헬퍼 메서드
    fun addAlarmGroup(alarmGroup: AlarmGroup) {
        alarmGroups[alarmGroup.id] = alarmGroup
        updateAlarmGroupsFlow()
    }

    fun addAlarm(groupId: Long, alarm: Alarm) {
        alarms.getOrPut(groupId) { mutableListOf() }.add(alarm)
        updateAlarmsFlow()
    }

    fun clear() {
        alarmGroups.clear()
        alarms.clear()
        nextId = 1L
        updateAlarmGroupsFlow()
        updateAlarmsFlow()
    }

    private fun updateAlarmGroupsFlow() {
        alarmGroupsFlow.value = alarmGroups.values.toList()
    }

    private fun updateAlarmsFlow() {
        alarmsFlow.value = alarms.mapValues { it.value.toList() }
    }

    override fun getAlarmGroupWithAlarms(id: Long): Flow<AlarmGroupWithAlarms> {
        return alarmsFlow.map { alarmsMap ->
            val group = alarmGroups[id]
            val groupAlarms = alarmsMap[id] ?: emptyList()

            if (group != null) {
                AlarmGroupWithAlarms(
                    group = group,
                    alarms = groupAlarms
                )
            } else {
                AlarmGroupWithAlarms(
                    group = AlarmGroup(
                        order = 0,
                        id = 0L,
                        title = "",
                        description = "",
                        includedAlarmsSize = 0,
                        repeatDays = emptySet(),
                        isActivated = false,
                        created = 0L,
                        updated = 0L,
                        isTemp = false
                    ),
                    alarms = emptyList()
                )
            }
        }
    }

    override fun getAlarmGroups(sort: AlarmMainSort): Flow<List<AlarmGroup>> {
        return alarmGroupsFlow.map { groups ->
            val filtered = groups.filter { !it.isTemp }
            when (sort) {
                AlarmMainSort.MOST_RECENT_CREATED -> filtered.sortedByDescending { it.created }
                AlarmMainSort.MOST_RECENT_UPDATED -> filtered.sortedByDescending { it.updated }
                AlarmMainSort.ACTIVATED_FIRST -> filtered.sortedByDescending { it.isActivated }
                AlarmMainSort.ALPHABETICAL -> filtered.sortedBy { it.title }
                AlarmMainSort.CUSTOM -> filtered.sortedBy { it.order }
            }
        }
    }

    override fun getTempAlarmGroups(): Flow<List<AlarmGroup>> {
        return alarmGroupsFlow.map { groups ->
            groups.filter { it.isTemp }
        }
    }

    override suspend fun getAlarmGroupById(id: Long): AlarmGroup? {
        return alarmGroups[id]
    }

    override suspend fun createAlarmGroup(alarmGroup: AlarmGroup): Long {
        val id = if (alarmGroup.id == 0L) nextId++ else alarmGroup.id
        val newGroup = alarmGroup.copy(id = id)
        alarmGroups[id] = newGroup
        updateAlarmGroupsFlow()
        return id
    }

    override suspend fun updateAlarmGroup(alarmGroup: AlarmGroup) {
        if (alarmGroups.containsKey(alarmGroup.id)) {
            alarmGroups[alarmGroup.id] = alarmGroup
            updateAlarmGroupsFlow()
        }
    }

    override suspend fun deleteAlarmGroup(alarmGroup: AlarmGroup) {
        alarmGroups.remove(alarmGroup.id)
        alarms.remove(alarmGroup.id)
        updateAlarmGroupsFlow()
        updateAlarmsFlow()
    }

    override suspend fun deleteSelectedAlarmGroups(alarmGroups: List<AlarmGroup>) {
        alarmGroups.forEach { group ->
            this.alarmGroups.remove(group.id)
            this.alarms.remove(group.id)
        }
        updateAlarmGroupsFlow()
        updateAlarmsFlow()
    }
}