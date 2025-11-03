package dev.loki.alarm.usecase.alarmgroup

import dev.loki.alarm.FakeAlarmGroupRepository
import dev.loki.alarm.FakeAlarmScheduler
import dev.loki.alarm.generateAlarm
import dev.loki.alarm.generateAlarmGroup
import dev.loki.alarmgroup.usecase.UpdateAlarmGroupUseCase
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UpdateAlarmGroupUseCaseTest {

    @Test
    fun `알람 그룹을 업데이트하면 Repository에 반영되어야 한다`() = runTest {
        // Given: 저장소에 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val scheduler = FakeAlarmScheduler()
        val useCase = UpdateAlarmGroupUseCase(repository, scheduler)

        val groupId = 1L
        val originalGroup = generateAlarmGroup(id = groupId, isActivate = true, isTemp = false)
        repository.addAlarmGroup(originalGroup)

        val updatedGroup = originalGroup.copy(
            title = "Updated Title",
            description = "Updated Description"
        )

        // When: 알람 그룹을 업데이트하면
        useCase(updatedGroup)

        // Then: 저장소에 변경사항이 반영되어야 한다
        val saved = repository.getAlarmGroupById(groupId)
        assertNotNull(saved)
        assertEquals("Updated Title", saved.title)
        assertEquals("Updated Description", saved.description)
    }

    @Test
    fun `활성화된 알람 그룹을 비활성화하면 모든 알람이 취소되어야 한다`() = runTest {
        // Given: 활성화된 알람 그룹과 알람들이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val scheduler = FakeAlarmScheduler()
        val useCase = UpdateAlarmGroupUseCase(repository, scheduler)

        val groupId = 1L
        val repeatDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)
        val group = generateAlarmGroup(id = groupId, isActivate = true, isTemp = false)
            .copy(repeatDays = repeatDays)
        repository.addAlarmGroup(group)

        val alarm1 = generateAlarm(id = 1L, groupId = groupId, isActivated = true)
        val alarm2 = generateAlarm(id = 2L, groupId = groupId, isActivated = true)
        repository.addAlarm(groupId, alarm1)
        repository.addAlarm(groupId, alarm2)

        val deactivatedGroup = group.copy(isActivated = false)

        // When: 알람 그룹을 비활성화하면
        useCase(deactivatedGroup)

        // Then: 모든 알람이 스케줄러에서 취소되어야 한다
        assertEquals(2, scheduler.canceledAlarms.size)
        assertTrue(scheduler.canceledAlarms.any { it.second.id == 1L })
        assertTrue(scheduler.canceledAlarms.any { it.second.id == 2L })
    }

    @Test
    fun `비활성화된 알람 그룹을 활성화하면 활성화된 알람만 스케줄링되어야 한다`() = runTest {
        // Given: 비활성화된 알람 그룹과 알람들이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val scheduler = FakeAlarmScheduler()
        val useCase = UpdateAlarmGroupUseCase(repository, scheduler)

        val groupId = 1L
        val repeatDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY)
        val group = generateAlarmGroup(id = groupId, isActivate = false, isTemp = false)
            .copy(repeatDays = repeatDays)
        repository.addAlarmGroup(group)

        val activeAlarm1 = generateAlarm(id = 1L, groupId = groupId, isActivated = true)
        val activeAlarm2 = generateAlarm(id = 2L, groupId = groupId, isActivated = true)
        val inactiveAlarm = generateAlarm(id = 3L, groupId = groupId, isActivated = false)
        repository.addAlarm(groupId, activeAlarm1)
        repository.addAlarm(groupId, activeAlarm2)
        repository.addAlarm(groupId, inactiveAlarm)

        val activatedGroup = group.copy(isActivated = true)

        // When: 알람 그룹을 활성화하면
        useCase(activatedGroup)

        // Then: 활성화된 알람만 스케줄링되어야 한다
        assertEquals(2, scheduler.scheduledAlarms.size)
        assertTrue(scheduler.scheduledAlarms.any { it.second.id == 1L })
        assertTrue(scheduler.scheduledAlarms.any { it.second.id == 2L })
        assertTrue(scheduler.scheduledAlarms.none { it.second.id == 3L })
    }

    @Test
    fun `활성화된 알람 그룹을 업데이트할 때 활성화 상태를 유지하면 알람이 다시 스케줄링되어야 한다`() = runTest {
        // Given: 활성화된 알람 그룹과 알람이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val scheduler = FakeAlarmScheduler()
        val useCase = UpdateAlarmGroupUseCase(repository, scheduler)

        val groupId = 1L
        val repeatDays = setOf(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)
        val group = generateAlarmGroup(id = groupId, isActivate = true, isTemp = false)
            .copy(repeatDays = repeatDays)
        repository.addAlarmGroup(group)

        val alarm = generateAlarm(id = 1L, groupId = groupId, isActivated = true)
        repository.addAlarm(groupId, alarm)

        val updatedGroup = group.copy(title = "Updated Title")

        // When: 활성화 상태를 유지하며 업데이트하면
        useCase(updatedGroup)

        // Then: 알람이 스케줄링되어야 한다
        assertTrue(scheduler.scheduledAlarms.isNotEmpty())
        assertEquals(repeatDays, scheduler.scheduledAlarms[0].first)
    }

    @Test
    fun `repeatDays를 포함한 알람 그룹을 업데이트할 때 올바른 repeatDays로 스케줄링되어야 한다`() = runTest {
        // Given: 알람 그룹과 알람이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val scheduler = FakeAlarmScheduler()
        val useCase = UpdateAlarmGroupUseCase(repository, scheduler)

        val groupId = 1L
        val repeatDays = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        val group = generateAlarmGroup(id = groupId, isActivate = false, isTemp = false)
            .copy(repeatDays = repeatDays)
        repository.addAlarmGroup(group)

        val alarm = generateAlarm(id = 1L, groupId = groupId, isActivated = true)
        repository.addAlarm(groupId, alarm)

        val activatedGroup = group.copy(isActivated = true)

        // When: 알람 그룹을 활성화하면
        useCase(activatedGroup)

        // Then: 올바른 repeatDays로 스케줄링되어야 한다
        assertEquals(1, scheduler.scheduledAlarms.size)
        assertEquals(repeatDays, scheduler.scheduledAlarms[0].first)
    }

    @Test
    fun `임시 알람 그룹을 업데이트할 수 있어야 한다`() = runTest {
        // Given: 임시 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val scheduler = FakeAlarmScheduler()
        val useCase = UpdateAlarmGroupUseCase(repository, scheduler)

        val groupId = 1L
        val tempGroup = generateAlarmGroup(id = groupId, isActivate = true, isTemp = true)
        repository.addAlarmGroup(tempGroup)

        val updatedGroup = tempGroup.copy(title = "Updated Temp Group")

        // When: 임시 알람 그룹을 업데이트하면
        useCase(updatedGroup)

        // Then: 업데이트가 반영되어야 한다
        val saved = repository.getAlarmGroupById(groupId)
        assertNotNull(saved)
        assertEquals("Updated Temp Group", saved.title)
        assertTrue(saved.isTemp)
    }

    @Test
    fun `여러 속성을 동시에 업데이트할 수 있어야 한다`() = runTest {
        // Given: 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val scheduler = FakeAlarmScheduler()
        val useCase = UpdateAlarmGroupUseCase(repository, scheduler)

        val groupId = 1L
        val group = generateAlarmGroup(id = groupId, isActivate = false, isTemp = false).copy(
            title = "Original Title",
            description = "Original Description",
            order = 0
        )
        repository.addAlarmGroup(group)

        val updatedGroup = group.copy(
            title = "New Title",
            description = "New Description",
            order = 5,
            isActivated = true
        )

        // When: 여러 속성을 업데이트하면
        useCase(updatedGroup)

        // Then: 모든 변경사항이 반영되어야 한다
        val saved = repository.getAlarmGroupById(groupId)
        assertNotNull(saved)
        assertEquals("New Title", saved.title)
        assertEquals("New Description", saved.description)
        assertEquals(5, saved.order)
        assertTrue(saved.isActivated)
    }

    @Test
    fun `알람 그룹 비활성화 시 알람과 repeatDays가 함께 전달되어야 한다`() = runTest {
        // Given: 활성화된 알람 그룹과 알람이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val scheduler = FakeAlarmScheduler()
        val useCase = UpdateAlarmGroupUseCase(repository, scheduler)

        val groupId = 1L
        val repeatDays = setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)
        val group = generateAlarmGroup(id = groupId, isActivate = true, isTemp = false)
            .copy(repeatDays = repeatDays)
        repository.addAlarmGroup(group)

        val alarm = generateAlarm(id = 1L, groupId = groupId, isActivated = true)
        repository.addAlarm(groupId, alarm)

        val deactivatedGroup = group.copy(isActivated = false)

        // When: 알람 그룹을 비활성화하면
        useCase(deactivatedGroup)

        // Then: 취소 시 올바른 repeatDays와 알람이 전달되어야 한다
        assertEquals(1, scheduler.canceledAlarms.size)
        val (canceledRepeatDays, canceledAlarm) = scheduler.canceledAlarms[0]
        assertEquals(repeatDays, canceledRepeatDays)
        assertEquals(1L, canceledAlarm.id)
    }

    @Test
    fun `순차적으로 여러 알람 그룹을 업데이트할 수 있어야 한다`() = runTest {
        // Given: 여러 알람 그룹이 저장되어 있고
        val repository = FakeAlarmGroupRepository()
        val scheduler = FakeAlarmScheduler()
        val useCase = UpdateAlarmGroupUseCase(repository, scheduler)

        val groups = (1L..3L).map { generateAlarmGroup(id = it, isActivate = false, isTemp = false) }
        groups.forEach { repository.addAlarmGroup(it) }

        // When: 순차적으로 업데이트하면
        groups.forEach { group ->
            val updated = group.copy(title = "Updated ${group.id}")
            useCase(updated)
        }

        // Then: 모든 업데이트가 반영되어야 한다
        (1L..3L).forEach { id ->
            val saved = repository.getAlarmGroupById(id)
            assertNotNull(saved)
            assertEquals("Updated $id", saved.title)
        }
    }
}