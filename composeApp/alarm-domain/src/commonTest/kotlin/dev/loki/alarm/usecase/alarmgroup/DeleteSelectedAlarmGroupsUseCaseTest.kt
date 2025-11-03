package dev.loki.alarm.usecase.alarmgroup

import dev.loki.alarm.FakeAlarmGroupRepository
import dev.loki.alarm.generateAlarmGroup
import dev.loki.alarmgroup.usecase.DeleteSelectedAlarmGroupsUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class DeleteSelectedAlarmGroupsUseCaseTest {

    @Test
    fun `선택된 여러 알람 그룹을 한 번에 삭제할 수 있어야 한다`() = runTest {
        // Given: 저장소에 여러 알람 그룹이 저장되어 있고
        val mockRepository = FakeAlarmGroupRepository()
        val useCase = DeleteSelectedAlarmGroupsUseCase(mockRepository)

        val alarmGroups = listOf(
            generateAlarmGroup(id = 1L),
            generateAlarmGroup(id = 2L),
            generateAlarmGroup(id = 3L)
        )

        alarmGroups.forEach { mockRepository.addAlarmGroup(it) }

        // When: 선택된 알람 그룹들을 삭제하면
        useCase.invoke(alarmGroups)

        // Then: 모든 선택된 알람 그룹이 저장소에서 삭제되어야 한다
        alarmGroups.forEach {
            assertNull(mockRepository.getAlarmGroupById(it.id))
        }
    }

    @Test
    fun `빈 리스트로 호출하면 아무 동작도 하지 않아야 한다`() = runTest {
        // Given: 저장소에 알람 그룹이 저장되어 있고
        val mockRepository = FakeAlarmGroupRepository()
        val useCase = DeleteSelectedAlarmGroupsUseCase(mockRepository)

        val existingGroup = generateAlarmGroup(id = 100L)
        mockRepository.addAlarmGroup(existingGroup)

        // When: 빈 리스트로 삭제를 요청하면
        useCase.invoke(emptyList())

        // Then: 기존 알람 그룹은 그대로 유지되어야 한다
        assertNotNull(mockRepository.getAlarmGroupById(100L))
    }

    @Test
    fun `선택된 그룹만 삭제되고 선택되지 않은 그룹은 유지되어야 한다`() = runTest {
        // Given: 저장소에 여러 알람 그룹이 저장되어 있고
        val mockRepository = FakeAlarmGroupRepository()
        val useCase = DeleteSelectedAlarmGroupsUseCase(mockRepository)

        val selectedGroups = listOf(
            generateAlarmGroup(id = 1L),
            generateAlarmGroup(id = 2L)
        )
        val unselectedGroup = generateAlarmGroup(id = 3L)

        selectedGroups.forEach { mockRepository.addAlarmGroup(it) }
        mockRepository.addAlarmGroup(unselectedGroup)

        // When: 선택된 알람 그룹만 삭제하면
        useCase.invoke(selectedGroups)

        // Then: 선택된 그룹은 삭제되고 선택되지 않은 그룹은 유지되어야 한다
        assertNull(mockRepository.getAlarmGroupById(1L))
        assertNull(mockRepository.getAlarmGroupById(2L))
        assertNotNull(mockRepository.getAlarmGroupById(3L))
    }

    @Test
    fun `활성화 상태가 다른 알람 그룹들을 함께 삭제할 수 있어야 한다`() = runTest {
        // Given: 저장소에 활성화/비활성화된 알람 그룹이 저장되어 있고
        val mockRepository = FakeAlarmGroupRepository()
        val useCase = DeleteSelectedAlarmGroupsUseCase(mockRepository)

        val mixedGroups = listOf(
            generateAlarmGroup(id = 1L, isActivate = true),
            generateAlarmGroup(id = 2L, isActivate = false),
            generateAlarmGroup(id = 3L, isActivate = true)
        )

        mixedGroups.forEach { mockRepository.addAlarmGroup(it) }

        // When: 활성화 상태가 다른 알람 그룹들을 삭제하면
        useCase.invoke(mixedGroups)

        // Then: 모든 알람 그룹이 삭제되어야 한다
        mixedGroups.forEach {
            assertNull(mockRepository.getAlarmGroupById(it.id))
        }
    }

    @Test
    fun `단일 알람 그룹도 리스트로 삭제할 수 있어야 한다`() = runTest {
        // Given: 저장소에 알람 그룹이 저장되어 있고
        val mockRepository = FakeAlarmGroupRepository()
        val useCase = DeleteSelectedAlarmGroupsUseCase(mockRepository)

        val singleGroup = generateAlarmGroup(id = 1L)
        mockRepository.addAlarmGroup(singleGroup)

        // When: 단일 알람 그룹을 리스트로 감싸서 삭제하면
        useCase.invoke(listOf(singleGroup))

        // Then: 알람 그룹이 삭제되어야 한다
        assertNull(mockRepository.getAlarmGroupById(1L))
    }

    @Test
    fun `많은 수의 알람 그룹을 한 번에 삭제할 수 있어야 한다`() = runTest {
        // Given: 저장소에 많은 알람 그룹이 저장되어 있고
        val mockRepository = FakeAlarmGroupRepository()
        val useCase = DeleteSelectedAlarmGroupsUseCase(mockRepository)

        val manyGroups = (1L..20L).map { generateAlarmGroup(id = it) }
        manyGroups.forEach { mockRepository.addAlarmGroup(it) }

        // When: 많은 수의 알람 그룹을 한 번에 삭제하면
        useCase.invoke(manyGroups)

        // Then: 모든 알람 그룹이 삭제되어야 한다
        manyGroups.forEach {
            assertNull(mockRepository.getAlarmGroupById(it.id))
        }
    }

    @Test
    fun `삭제 전후 알람 그룹 수를 확인할 수 있어야 한다`() = runTest {
        // Given: 저장소에 알람 그룹들이 저장되어 있고
        val mockRepository = FakeAlarmGroupRepository()
        val useCase = DeleteSelectedAlarmGroupsUseCase(mockRepository)

        val allGroups = (1L..5L).map { generateAlarmGroup(id = it) }
        allGroups.forEach { mockRepository.addAlarmGroup(it) }

        val selectedGroups = allGroups.take(3)

        // When: 일부 알람 그룹을 삭제하면
        useCase.invoke(selectedGroups)

        // Then: 선택된 그룹은 삭제되고 나머지는 유지되어야 한다
        selectedGroups.forEach {
            assertNull(mockRepository.getAlarmGroupById(it.id))
        }
        allGroups.drop(3).forEach {
            assertNotNull(mockRepository.getAlarmGroupById(it.id))
        }
    }
}