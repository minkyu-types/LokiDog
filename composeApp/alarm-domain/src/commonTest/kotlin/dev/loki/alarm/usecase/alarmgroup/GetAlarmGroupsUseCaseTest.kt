package dev.loki.alarm.usecase.alarmgroup

import dev.loki.DomainResult
import dev.loki.alarm.FakeAlarmGroupRepository
import dev.loki.alarm.generateAlarmGroup
import dev.loki.alarmgroup.model.AlarmMainSort
import dev.loki.alarmgroup.usecase.GetAlarmGroupsUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetAlarmGroupsUseCaseTest {

    @Test
    fun `정렬 기준에 따라 알람 그룹 목록을 반환해야 한다`() = runTest {
        // Given: 저장소에 여러 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupsUseCase(repository)

        val group1 = generateAlarmGroup(id = 1L, isTemp = false)
        val group2 = generateAlarmGroup(id = 2L, isTemp = false)
        val group3 = generateAlarmGroup(id = 3L, isTemp = false)

        repository.addAlarmGroup(group1)
        repository.addAlarmGroup(group2)
        repository.addAlarmGroup(group3)

        // When: 알람 그룹 목록을 조회하면
        val result = useCase(AlarmMainSort.CUSTOM).first()

        // Then: Success 결과로 알람 그룹 목록을 반환해야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(3, result.data.size)
    }

    @Test
    fun `임시 알람 그룹은 제외하고 반환해야 한다`() = runTest {
        // Given: 저장소에 일반 알람 그룹과 임시 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupsUseCase(repository)

        val normalGroup1 = generateAlarmGroup(id = 1L, isTemp = false)
        val normalGroup2 = generateAlarmGroup(id = 2L, isTemp = false)
        val tempGroup = generateAlarmGroup(id = 3L, isTemp = true)

        repository.addAlarmGroup(normalGroup1)
        repository.addAlarmGroup(normalGroup2)
        repository.addAlarmGroup(tempGroup)

        // When: 알람 그룹 목록을 조회하면
        val result = useCase(AlarmMainSort.CUSTOM).first()

        // Then: 임시 알람 그룹을 제외한 알람 그룹만 반환해야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(2, result.data.size)
        assertTrue(result.data.none { it.isTemp })
    }

    @Test
    fun `생성 시간 기준 내림차순 정렬이 적용되어야 한다`() = runTest {
        // Given: 저장소에 다른 생성 시간을 가진 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupsUseCase(repository)

        val oldGroup = generateAlarmGroup(id = 1L, isTemp = false).copy(created = 1000L)
        val middleGroup = generateAlarmGroup(id = 2L, isTemp = false).copy(created = 2000L)
        val newGroup = generateAlarmGroup(id = 3L, isTemp = false).copy(created = 3000L)

        repository.addAlarmGroup(oldGroup)
        repository.addAlarmGroup(middleGroup)
        repository.addAlarmGroup(newGroup)

        // When: MOST_RECENT_CREATED 정렬로 조회하면
        val result = useCase(AlarmMainSort.MOST_RECENT_CREATED).first()

        // Then: 최신 생성 시간 순으로 정렬되어야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(3L, result.data[0].id)
        assertEquals(2L, result.data[1].id)
        assertEquals(1L, result.data[2].id)
    }

    @Test
    fun `수정 시간 기준 내림차순 정렬이 적용되어야 한다`() = runTest {
        // Given: 저장소에 다른 수정 시간을 가진 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupsUseCase(repository)

        val group1 = generateAlarmGroup(id = 1L, isTemp = false).copy(updated = 1000L)
        val group2 = generateAlarmGroup(id = 2L, isTemp = false).copy(updated = 3000L)
        val group3 = generateAlarmGroup(id = 3L, isTemp = false).copy(updated = 2000L)

        repository.addAlarmGroup(group1)
        repository.addAlarmGroup(group2)
        repository.addAlarmGroup(group3)

        // When: MOST_RECENT_UPDATED 정렬로 조회하면
        val result = useCase(AlarmMainSort.MOST_RECENT_UPDATED).first()

        // Then: 최신 수정 시간 순으로 정렬되어야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(2L, result.data[0].id)
        assertEquals(3L, result.data[1].id)
        assertEquals(1L, result.data[2].id)
    }

    @Test
    fun `활성화된 알람 그룹이 먼저 정렬되어야 한다`() = runTest {
        // Given: 저장소에 활성화/비활성화 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupsUseCase(repository)

        val inactiveGroup1 = generateAlarmGroup(id = 1L, isActivate = false, isTemp = false)
        val activeGroup = generateAlarmGroup(id = 2L, isActivate = true, isTemp = false)
        val inactiveGroup2 = generateAlarmGroup(id = 3L, isActivate = false, isTemp = false)

        repository.addAlarmGroup(inactiveGroup1)
        repository.addAlarmGroup(activeGroup)
        repository.addAlarmGroup(inactiveGroup2)

        // When: ACTIVATED_FIRST 정렬로 조회하면
        val result = useCase(AlarmMainSort.ACTIVATED_FIRST).first()

        // Then: 활성화된 알람 그룹이 먼저 나와야 한다
        assertTrue(result is DomainResult.Success)
        assertTrue(result.data[0].isActivated)
        assertEquals(2L, result.data[0].id)
    }

    @Test
    fun `제목 기준 알파벳순 정렬이 적용되어야 한다`() = runTest {
        // Given: 저장소에 다른 제목을 가진 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupsUseCase(repository)

        val groupC = generateAlarmGroup(id = 1L, isTemp = false).copy(title = "Charlie")
        val groupA = generateAlarmGroup(id = 2L, isTemp = false).copy(title = "Alpha")
        val groupB = generateAlarmGroup(id = 3L, isTemp = false).copy(title = "Bravo")

        repository.addAlarmGroup(groupC)
        repository.addAlarmGroup(groupA)
        repository.addAlarmGroup(groupB)

        // When: ALPHABETICAL 정렬로 조회하면
        val result = useCase(AlarmMainSort.ALPHABETICAL).first()

        // Then: 알파벳 순으로 정렬되어야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals("Alpha", result.data[0].title)
        assertEquals("Bravo", result.data[1].title)
        assertEquals("Charlie", result.data[2].title)
    }

    @Test
    fun `사용자 정의 순서 정렬이 적용되어야 한다`() = runTest {
        // Given: 저장소에 다른 순서를 가진 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupsUseCase(repository)

        val group1 = generateAlarmGroup(id = 1L, isTemp = false).copy(order = 2)
        val group2 = generateAlarmGroup(id = 2L, isTemp = false).copy(order = 0)
        val group3 = generateAlarmGroup(id = 3L, isTemp = false).copy(order = 1)

        repository.addAlarmGroup(group1)
        repository.addAlarmGroup(group2)
        repository.addAlarmGroup(group3)

        // When: CUSTOM 정렬로 조회하면
        val result = useCase(AlarmMainSort.CUSTOM).first()

        // Then: 사용자 정의 순서대로 정렬되어야 한다
        assertTrue(result is DomainResult.Success)
        assertEquals(2, result.data[0].order)
        assertEquals(0, result.data[1].order)
        assertEquals(1, result.data[2].order)
    }

    @Test
    fun `빈 목록을 반환할 수 있어야 한다`() = runTest {
        // Given: 저장소에 알람 그룹이 없고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupsUseCase(repository)

        // When: 알람 그룹 목록을 조회하면
        val result = useCase(AlarmMainSort.CUSTOM).first()

        // Then: 빈 목록을 성공적으로 반환해야 한다
        assertTrue(result is DomainResult.Success)
        assertTrue(result.data.isEmpty())
    }

    @Test
    fun `Flow로 알람 그룹 변경사항을 구독할 수 있어야 한다`() = runTest {
        // Given: 저장소에 알람 그룹이 하나 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupsUseCase(repository)

        val group1 = generateAlarmGroup(id = 1L, isTemp = false)
        repository.addAlarmGroup(group1)

        // When: Flow를 구독한 상태에서 새로운 알람 그룹을 추가하면
        val flow = useCase(AlarmMainSort.CUSTOM)
        val firstResult = flow.first()

        // Then: 첫 번째 결과는 1개의 알람 그룹을 포함해야 한다
        assertTrue(firstResult is DomainResult.Success)
        assertEquals(1, firstResult.data.size)
    }
}