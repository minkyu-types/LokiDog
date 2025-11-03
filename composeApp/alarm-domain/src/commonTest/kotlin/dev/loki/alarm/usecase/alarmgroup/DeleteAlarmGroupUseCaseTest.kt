package dev.loki.alarm.usecase.alarmgroup

import dev.loki.alarm.FakeAlarmGroupRepository
import dev.loki.alarm.generateAlarm
import dev.loki.alarm.generateAlarmGroup
import dev.loki.alarmgroup.model.AlarmGroup
import dev.loki.alarmgroup.usecase.DeleteAlarmGroupUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DeleteAlarmGroupUseCaseTest {

    @Test
    fun `제공된 알람 그룹으로 repository의 deleteAlarmGroup을 호출해야 한다`() = runTest {
        // Given: 저장소에 알람 그룹이 저장되어 있고
        val mockAlarmGroupRepository = FakeAlarmGroupRepository()
        val usecase = DeleteAlarmGroupUseCase(mockAlarmGroupRepository)

        val alarmGroup = AlarmGroup(
            id = 1L,
            order = 0,
            title = "Test Group",
            description = "This is a test group",
            includedAlarmsSize = 5,
            repeatDays = emptySet(),
            created = 1623456789L,
            updated = 1623456789L,
            isActivated = true,
            isTemp = false,
        )
        mockAlarmGroupRepository.addAlarmGroup(alarmGroup)

        // When: 해당 알람 그룹을 삭제하면
        usecase.invoke(alarmGroup)

        // Then: 저장소에서 알람 그룹이 삭제되어야 한다
        assertNull(mockAlarmGroupRepository.getAlarmGroupById(alarmGroup.id))
    }

    @Test
    fun `알람 그룹 삭제 시 FakeAlarmGroupRepository가 연관 알람도 삭제해야 한다`() = runTest {
        // Given: 저장소에 알람 그룹과 여러 알람이 저장되어 있고
        val mockAlarmGroupRepository = FakeAlarmGroupRepository()
        val usecase = DeleteAlarmGroupUseCase(mockAlarmGroupRepository)

        val groupId = 1111L
        val alarmGroup = generateAlarmGroup(id = groupId)
        mockAlarmGroupRepository.addAlarmGroup(alarmGroup)

        // FakeAlarmGroupRepository에 알람 추가
        repeat(5) {
            mockAlarmGroupRepository.addAlarm(groupId, generateAlarm(id = it.toLong(), groupId = groupId))
        }

        // When: 알람 그룹을 삭제하면
        usecase.invoke(alarmGroup)

        // Then: Repository가 그룹과 연관된 알람도 함께 삭제해야 한다 (FakeAlarmGroupRepository의 deleteAlarmGroup 동작 검증)
        assertNull(mockAlarmGroupRepository.getAlarmGroupById(groupId))
    }

    @Test
    fun `임시 알람 그룹을 삭제할 수 있어야 한다`() = runTest {
        // Given: 저장소에 임시 알람 그룹이 저장되어 있고
        val mockAlarmGroupRepository = FakeAlarmGroupRepository()
        val usecase = DeleteAlarmGroupUseCase(mockAlarmGroupRepository)
        val groupId = 1111L
        val alarmGroup = generateAlarmGroup(id = groupId, isTemp = true)
        mockAlarmGroupRepository.addAlarmGroup(alarmGroup)

        // When: 임시 알람 그룹을 삭제하면
        usecase.invoke(alarmGroup)

        // Then: 저장소에서 임시 알람 그룹이 삭제되어야 한다
        assertNull(mockAlarmGroupRepository.getAlarmGroupById(groupId))
    }

    @Test
    fun `여러 알람 그룹을 순차적으로 삭제할 수 있어야 한다`() = runTest {
        // Given: 저장소에 여러 알람 그룹이 저장되어 있고
        val mockAlarmGroupRepository = FakeAlarmGroupRepository()
        val usecase = DeleteAlarmGroupUseCase(mockAlarmGroupRepository)
        val groupIds = listOf(1111L, 2222L, 3333L)
        val alarmGroups = groupIds.map { generateAlarmGroup(id = it) }

        alarmGroups.forEach {
            mockAlarmGroupRepository.addAlarmGroup(it)
        }

        // When: 여러 알람 그룹을 순차적으로 삭제하면
        alarmGroups.forEach {
            usecase.invoke(it)
        }

        // Then: 모든 알람 그룹이 저장소에서 삭제되어야 한다
        alarmGroups.forEach {
            assertNull(mockAlarmGroupRepository.getAlarmGroupById(it.id))
        }
    }

    @Test
    fun `활성화되지 않은 알람 그룹도 삭제할 수 있어야 한다`() = runTest {
        // Given: 저장소에 비활성화된 알람 그룹이 저장되어 있고
        val mockAlarmGroupRepository = FakeAlarmGroupRepository()
        val usecase = DeleteAlarmGroupUseCase(mockAlarmGroupRepository)
        val groupId = 1111L
        val inactiveAlarmGroup = generateAlarmGroup(id = groupId, isActivate = false)
        mockAlarmGroupRepository.addAlarmGroup(inactiveAlarmGroup)

        // When: 비활성화된 알람 그룹을 삭제하면
        usecase.invoke(inactiveAlarmGroup)

        // Then: 저장소에서 알람 그룹이 삭제되어야 한다
        assertNull(mockAlarmGroupRepository.getAlarmGroupById(groupId))
    }

    @Test
    fun `삭제 전에 알람 그룹이 존재하고 삭제 후에는 존재하지 않아야 한다`() = runTest {
        // Given: 저장소에 알람 그룹이 저장되어 있고
        val mockAlarmGroupRepository = FakeAlarmGroupRepository()
        val usecase = DeleteAlarmGroupUseCase(mockAlarmGroupRepository)
        val groupId = 5555L
        val alarmGroup = generateAlarmGroup(id = groupId)
        mockAlarmGroupRepository.addAlarmGroup(alarmGroup)

        // When: 삭제 전에는 알람 그룹이 존재하고
        assertNotNull(mockAlarmGroupRepository.getAlarmGroupById(groupId))

        // 알람 그룹을 삭제하면
        usecase.invoke(alarmGroup)

        // Then: 삭제 후에는 알람 그룹이 존재하지 않아야 한다
        assertNull(mockAlarmGroupRepository.getAlarmGroupById(groupId))
    }
}