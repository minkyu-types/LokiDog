package dev.loki.alarm.usecase.alarmgroup

import dev.loki.DomainResult
import dev.loki.alarm.FakeAlarmGroupRepository
import dev.loki.alarm.generateAlarmGroup
import dev.loki.alarmgroup.usecase.GetTempAlarmGroupsUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetTempAlarmGroupsUseCaseTest {

    @Test
    fun `임시 알람 그룹 목록만 반환해야 한다`() = runTest {
        // Given: 저장소에 일반 알람 그룹과 임시 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetTempAlarmGroupsUseCase(repository)

        val normalGroup1 = generateAlarmGroup(id = 1L, isTemp = false)
        val normalGroup2 = generateAlarmGroup(id = 2L, isTemp = false)
        val tempGroup1 = generateAlarmGroup(id = 3L, isTemp = true)
        val tempGroup2 = generateAlarmGroup(id = 4L, isTemp = true)

        repository.addAlarmGroup(normalGroup1)
        repository.addAlarmGroup(normalGroup2)
        repository.addAlarmGroup(tempGroup1)
        repository.addAlarmGroup(tempGroup2)

        // When: 임시 알람 그룹 목록을 조회하면
        val result = useCase().first()

        // Then: Success 결과로 임시 알람 그룹만 반환해야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(2, result.data.size)
        assertTrue(result.data.all { it.isTemp })
    }

    @Test
    fun `일반 알람 그룹은 포함하지 않아야 한다`() = runTest {
        // Given: 저장소에 일반 알람 그룹과 임시 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetTempAlarmGroupsUseCase(repository)

        val normalGroup = generateAlarmGroup(id = 1L, isTemp = false)
        val tempGroup = generateAlarmGroup(id = 2L, isTemp = true)

        repository.addAlarmGroup(normalGroup)
        repository.addAlarmGroup(tempGroup)

        // When: 임시 알람 그룹 목록을 조회하면
        val result = useCase().first()

        // Then: 일반 알람 그룹을 포함하지 않아야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(1, result.data.size)
        assertEquals(2L, result.data[0].id)
        assertTrue(result.data[0].isTemp)
    }

    @Test
    fun `임시 알람 그룹이 없으면 빈 목록을 반환해야 한다`() = runTest {
        // Given: 저장소에 일반 알람 그룹만 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetTempAlarmGroupsUseCase(repository)

        val normalGroup1 = generateAlarmGroup(id = 1L, isTemp = false)
        val normalGroup2 = generateAlarmGroup(id = 2L, isTemp = false)

        repository.addAlarmGroup(normalGroup1)
        repository.addAlarmGroup(normalGroup2)

        // When: 임시 알람 그룹 목록을 조회하면
        val result = useCase().first()

        // Then: 빈 목록을 반환해야 한다
        assertTrue(result is DomainResult.Success)
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `저장소가 비어있으면 빈 목록을 반환해야 한다`() = runTest {
        // Given: 저장소가 비어있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetTempAlarmGroupsUseCase(repository)

        // When: 임시 알람 그룹 목록을 조회하면
        val result = useCase().first()

        // Then: 빈 목록을 성공적으로 반환해야 한다
        assertTrue(result is DomainResult.Success)
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `여러 개의 임시 알람 그룹을 모두 반환해야 한다`() = runTest {
        // Given: 저장소에 여러 개의 임시 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetTempAlarmGroupsUseCase(repository)

        repeat(5) { index ->
            val tempGroup = generateAlarmGroup(id = index.toLong(), isTemp = true)
            repository.addAlarmGroup(tempGroup)
        }

        // When: 임시 알람 그룹 목록을 조회하면
        val result = useCase().first()

        // Then: 모든 임시 알람 그룹을 반환해야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(5, result.data.size)
        assertTrue(result.data.all { it.isTemp })
    }

    @Test
    fun `활성화된 임시 알람 그룹도 포함해야 한다`() = runTest {
        // Given: 저장소에 활성화/비활성화 임시 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetTempAlarmGroupsUseCase(repository)

        val activeTempGroup = generateAlarmGroup(id = 1L, isActivate = true, isTemp = true)
        val inactiveTempGroup = generateAlarmGroup(id = 2L, isActivate = false, isTemp = true)

        repository.addAlarmGroup(activeTempGroup)
        repository.addAlarmGroup(inactiveTempGroup)

        // When: 임시 알람 그룹 목록을 조회하면
        val result = useCase().first()

        // Then: 활성화 상태와 관계없이 모든 임시 알람 그룹을 반환해야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(2, result.data.size)
        assertEquals(1, result.data.count { it.isActivated })
        assertEquals(1, result.data.count { !it.isActivated })
    }

    @Test
    fun `Flow로 임시 알람 그룹의 변경사항을 구독할 수 있어야 한다`() = runTest {
        // Given: 저장소에 임시 알람 그룹이 하나 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetTempAlarmGroupsUseCase(repository)

        val tempGroup = generateAlarmGroup(id = 1L, isTemp = true)
        repository.addAlarmGroup(tempGroup)

        // When: Flow를 구독한 상태에서 조회하면
        val flow = useCase()
        val result = flow.first()

        // Then: 임시 알람 그룹을 받을 수 있어야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(1, result.data.size)
        assertTrue(result.data[0].isTemp)
    }

    @Test
    fun `임시 알람 그룹 목록은 일반 알람 그룹 목록과 독립적이어야 한다`() = runTest {
        // Given: 저장소에 다양한 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetTempAlarmGroupsUseCase(repository)

        val normalGroups = (1L..10L).map { generateAlarmGroup(id = it, isTemp = false) }
        val tempGroups = (11L..13L).map { generateAlarmGroup(id = it, isTemp = true) }

        normalGroups.forEach { repository.addAlarmGroup(it) }
        tempGroups.forEach { repository.addAlarmGroup(it) }

        // When: 임시 알람 그룹 목록을 조회하면
        val result = useCase().first()

        // Then: 임시 알람 그룹만 반환하고 일반 알람 그룹의 개수에 영향받지 않아야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(3, result.data.size)
        assertTrue(result.data.all { it.id in 11L..13L })
        assertTrue(result.data.all { it.isTemp })
    }

    @Test
    fun `임시 알람 그룹의 다양한 속성이 모두 보존되어야 한다`() = runTest {
        // Given: 저장소에 다양한 속성을 가진 임시 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetTempAlarmGroupsUseCase(repository)

        val tempGroup = generateAlarmGroup(id = 1L, isTemp = true).copy(
            title = "Test Temp Group",
            description = "Test Description",
            includedAlarmsSize = 3
        )
        repository.addAlarmGroup(tempGroup)

        // When: 임시 알람 그룹 목록을 조회하면
        val result = useCase().first()

        // Then: 모든 속성이 보존되어야 한다
        assertTrue(result is DomainResult.Success)
        val retrieved = result.data[0]
        assertEquals("Test Temp Group", retrieved.title)
        assertEquals("Test Description", retrieved.description)
        assertEquals(3, retrieved.includedAlarmsSize)
        assertTrue(retrieved.isTemp)
    }
}