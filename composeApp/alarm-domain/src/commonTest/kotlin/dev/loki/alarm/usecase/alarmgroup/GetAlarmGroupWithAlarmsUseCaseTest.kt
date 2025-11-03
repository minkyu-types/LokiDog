package dev.loki.alarm.usecase.alarmgroup

import dev.loki.DomainResult
import dev.loki.alarm.FakeAlarmGroupRepository
import dev.loki.alarm.generateAlarm
import dev.loki.alarm.generateAlarmGroup
import dev.loki.alarmgroup.usecase.GetAlarmGroupWithAlarmsUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetAlarmGroupWithAlarmsUseCaseTest {

    @Test
    fun `알람 그룹과 연관된 알람 목록을 반환해야 한다`() = runTest {
        // Given: 저장소에 알람 그룹과 알람들이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupWithAlarmsUseCase(repository)

        val groupId = 1L
        val group = generateAlarmGroup(id = groupId)
        repository.addAlarmGroup(group)

        val alarm1 = generateAlarm(id = 1L, groupId = groupId)
        val alarm2 = generateAlarm(id = 2L, groupId = groupId)
        val alarm3 = generateAlarm(id = 3L, groupId = groupId)

        repository.addAlarm(groupId, alarm1)
        repository.addAlarm(groupId, alarm2)
        repository.addAlarm(groupId, alarm3)

        // When: 알람 그룹 ID로 조회하면
        val result = useCase(groupId).first()

        // Then: Success 결과로 알람 그룹과 알람 목록을 반환해야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(groupId, result.data.group.id)
        assertEquals(3, result.data.alarms.size)
    }

    @Test
    fun `여러 알람을 포함한 알람 그룹을 조회할 수 있어야 한다`() = runTest {
        // Given: 저장소에 여러 알람을 포함한 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupWithAlarmsUseCase(repository)

        val groupId = 1L
        val group = generateAlarmGroup(id = groupId)
        repository.addAlarmGroup(group)

        repeat(10) { index ->
            val alarm = generateAlarm(id = index.toLong(), groupId = groupId)
            repository.addAlarm(groupId, alarm)
        }

        // When: 알람 그룹 ID로 조회하면
        val result = useCase(groupId).first()

        // Then: 모든 알람이 포함되어야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(10, result.data.alarms.size)
    }

    @Test
    fun `활성화된 알람과 비활성화된 알람을 모두 포함해야 한다`() = runTest {
        // Given: 저장소에 활성화/비활성화 알람을 포함한 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupWithAlarmsUseCase(repository)

        val groupId = 1L
        val group = generateAlarmGroup(id = groupId)
        repository.addAlarmGroup(group)

        val activeAlarm1 = generateAlarm(id = 1L, groupId = groupId, isActivated = true)
        val activeAlarm2 = generateAlarm(id = 2L, groupId = groupId, isActivated = true)
        val inactiveAlarm = generateAlarm(id = 3L, groupId = groupId, isActivated = false)

        repository.addAlarm(groupId, activeAlarm1)
        repository.addAlarm(groupId, activeAlarm2)
        repository.addAlarm(groupId, inactiveAlarm)

        // When: 알람 그룹 ID로 조회하면
        val result = useCase(groupId).first()

        // Then: 활성화/비활성화 상태와 관계없이 모든 알람이 포함되어야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(3, result.data.alarms.size)
        assertEquals(2, result.data.alarms.count { it.isActivated })
        assertEquals(1, result.data.alarms.count { !it.isActivated })
    }

    @Test
    fun `다른 그룹의 알람은 포함하지 않아야 한다`() = runTest {
        // Given: 저장소에 여러 알람 그룹과 알람들이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupWithAlarmsUseCase(repository)

        val groupId1 = 1L
        val groupId2 = 2L

        val group1 = generateAlarmGroup(id = groupId1)
        val group2 = generateAlarmGroup(id = groupId2)
        repository.addAlarmGroup(group1)
        repository.addAlarmGroup(group2)

        // 그룹1의 알람
        val alarm1ForGroup1 = generateAlarm(id = 1L, groupId = groupId1)
        val alarm2ForGroup1 = generateAlarm(id = 2L, groupId = groupId1)
        repository.addAlarm(groupId1, alarm1ForGroup1)
        repository.addAlarm(groupId1, alarm2ForGroup1)

        // 그룹2의 알람
        val alarm1ForGroup2 = generateAlarm(id = 3L, groupId = groupId2)
        repository.addAlarm(groupId2, alarm1ForGroup2)

        // When: 그룹1을 조회하면
        val result = useCase(groupId1).first()

        // Then: 그룹1의 알람만 포함되어야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(groupId1, result.data.group.id)
        assertEquals(2, result.data.alarms.size)
        assertTrue(result.data.alarms.all { it.groupId == groupId1 })
    }

    @Test
    fun `존재하지 않는 알람 그룹 ID로 조회하면 기본값을 반환해야 한다`() = runTest {
        // Given: 저장소가 비어있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupWithAlarmsUseCase(repository)

        val nonExistentGroupId = 999L

        // When: 존재하지 않는 알람 그룹 ID로 조회하면
        val result = useCase(nonExistentGroupId).first()

        // Then: Success와 함께 기본 빈 알람 그룹을 반환해야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(0L, result.data.group.id)
        assertTrue(result.data.alarms.isEmpty())
    }

    @Test
    fun `Flow로 알람 그룹과 알람의 변경사항을 구독할 수 있어야 한다`() = runTest {
        // Given: 저장소에 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupWithAlarmsUseCase(repository)

        val groupId = 1L
        val group = generateAlarmGroup(id = groupId)
        repository.addAlarmGroup(group)

        // When: Flow를 구독하면
        val flow = useCase(groupId)
        val result = flow.first()

        // Then: 알람 그룹 정보를 받을 수 있어야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(groupId, result.data.group.id)
    }

    @Test
    fun `임시 알람 그룹과 연관된 알람을 조회할 수 있어야 한다`() = runTest {
        // Given: 저장소에 임시 알람 그룹과 알람이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupWithAlarmsUseCase(repository)

        val groupId = 1L
        val tempGroup = generateAlarmGroup(id = groupId, isTemp = true)
        repository.addAlarmGroup(tempGroup)

        val alarm = generateAlarm(id = 1L, groupId = groupId)
        repository.addAlarm(groupId, alarm)

        // When: 임시 알람 그룹 ID로 조회하면
        val result = useCase(groupId).first()

        // Then: 임시 알람 그룹과 연관된 알람을 반환해야 한다
        assertTrue(result is DomainResult.Success)
        assertTrue(result.data.group.isTemp)
        assertEquals(1, result.data.alarms.size)
    }
}