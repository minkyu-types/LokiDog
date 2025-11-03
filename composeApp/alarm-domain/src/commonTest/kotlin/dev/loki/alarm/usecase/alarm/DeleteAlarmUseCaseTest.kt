package dev.loki.alarm.usecase.alarm

import dev.loki.AlarmScheduler
import dev.loki.alarm.model.Alarm
import dev.loki.alarm.repository.AlarmRepository
import dev.loki.alarm.usecase.DeleteAlarmUseCase
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DeleteAlarmUseCaseTest {

    @Test
    fun `제공된 알람으로 repository의 deleteAlarm을 호출해야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val mockScheduler = FakeAlarmScheduler()
        val useCase = DeleteAlarmUseCase(mockRepository, mockScheduler)

        val testAlarm = Alarm(
            id = 1L,
            groupId = 100L,
            time = "09:00",
            memo = "Morning Alarm",
            isActivated = true,
            isTemp = false
        )

        // 먼저 알람을 추가
        mockRepository.upsertAlarm(testAlarm)

        // When
        useCase.invoke(testAlarm)

        // Then
        assertTrue(mockRepository.deletedAlarms.contains(testAlarm))
        assertEquals(1, mockRepository.deletedAlarms.size)
        assertNull(mockRepository.getAlarmById(testAlarm.id))
    }

    @Test
    fun `알람 삭제 시 스케줄러의 cancel을 호출해야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val mockScheduler = FakeAlarmScheduler()
        val useCase = DeleteAlarmUseCase(mockRepository, mockScheduler)

        val testAlarm = Alarm(
            id = 1L,
            groupId = 100L,
            time = "09:00",
            memo = "Morning Alarm",
            isActivated = true,
            isTemp = false
        )

        mockRepository.upsertAlarm(testAlarm)

        // When
        useCase.invoke(testAlarm)

        // Then
        assertEquals(1, mockScheduler.canceledAlarms.size)
        assertEquals(testAlarm, mockScheduler.canceledAlarms.first().second)
        assertEquals(emptySet(), mockScheduler.canceledAlarms.first().first)
    }

    @Test
    fun `활성화되지 않은 알람도 삭제할 수 있어야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val mockScheduler = FakeAlarmScheduler()
        val useCase = DeleteAlarmUseCase(mockRepository, mockScheduler)

        val inactiveAlarm = Alarm(
            id = 2L,
            groupId = 200L,
            time = "14:30",
            memo = "Inactive Alarm",
            isActivated = false,
            isTemp = false
        )

        mockRepository.upsertAlarm(inactiveAlarm)

        // When
        useCase.invoke(inactiveAlarm)

        // Then
        assertTrue(mockRepository.deletedAlarms.contains(inactiveAlarm))
        assertNull(mockRepository.getAlarmById(inactiveAlarm.id))
    }

    @Test
    fun `임시 알람을 삭제할 수 있어야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val mockScheduler = FakeAlarmScheduler()
        val useCase = DeleteAlarmUseCase(mockRepository, mockScheduler)

        val tempAlarm = Alarm(
            id = 0L,
            groupId = 0L,
            time = "20:00",
            memo = "Temp Alarm",
            isActivated = true,
            isTemp = true
        )

        mockRepository.upsertAlarm(tempAlarm)

        // When
        useCase.invoke(tempAlarm)

        // Then
        assertEquals(1, mockRepository.deletedAlarms.size)
        assertTrue(mockRepository.deletedAlarms.first().isTemp)
    }

    @Test
    fun `여러 알람을 순차적으로 삭제할 수 있어야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val mockScheduler = FakeAlarmScheduler()
        val useCase = DeleteAlarmUseCase(mockRepository, mockScheduler)

        val alarm1 = Alarm(1L, 100L, "08:00", "First", true, false)
        val alarm2 = Alarm(2L, 100L, "09:00", "Second", true, false)
        val alarm3 = Alarm(3L, 200L, "10:00", "Third", false, false)

        // 알람들을 먼저 추가
        mockRepository.upsertAlarm(alarm1)
        mockRepository.upsertAlarm(alarm2)
        mockRepository.upsertAlarm(alarm3)

        // When
        useCase.invoke(alarm1)
        useCase.invoke(alarm2)
        useCase.invoke(alarm3)

        // Then
        assertEquals(3, mockRepository.deletedAlarms.size)
        assertTrue(mockRepository.deletedAlarms.containsAll(listOf(alarm1, alarm2, alarm3)))
        assertNull(mockRepository.getAlarmById(alarm1.id))
        assertNull(mockRepository.getAlarmById(alarm2.id))
        assertNull(mockRepository.getAlarmById(alarm3.id))
    }

    @Test
    fun `같은 그룹의 알람들을 삭제할 수 있어야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val mockScheduler = FakeAlarmScheduler()
        val useCase = DeleteAlarmUseCase(mockRepository, mockScheduler)

        val groupId = 100L
        val alarm1 = Alarm(1L, groupId, "08:00", "First", true, false)
        val alarm2 = Alarm(2L, groupId, "09:00", "Second", true, false)

        mockRepository.upsertAlarm(alarm1)
        mockRepository.upsertAlarm(alarm2)

        // When
        useCase.invoke(alarm1)
        useCase.invoke(alarm2)

        // Then
        assertEquals(2, mockRepository.deletedAlarms.size)
        assertTrue(mockRepository.getAlarmsByGroupId(groupId).isEmpty())
    }

    @Test
    fun `알람 삭제 후 스케줄러 취소가 실행되어야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val mockScheduler = FakeAlarmScheduler()
        val useCase = DeleteAlarmUseCase(mockRepository, mockScheduler)

        val alarm1 = Alarm(1L, 100L, "08:00", "First", true, false)
        val alarm2 = Alarm(2L, 200L, "09:00", "Second", true, false)

        mockRepository.upsertAlarm(alarm1)
        mockRepository.upsertAlarm(alarm2)

        // When
        useCase.invoke(alarm1)
        useCase.invoke(alarm2)

        // Then
        assertEquals(2, mockScheduler.canceledAlarms.size)
        assertEquals(alarm1, mockScheduler.canceledAlarms[0].second)
        assertEquals(alarm2, mockScheduler.canceledAlarms[1].second)
    }

    // Fake implementations for testing
    private class FakeAlarmRepository : AlarmRepository {
        val deletedAlarms = mutableListOf<Alarm>()
        private val alarms = mutableMapOf<Long, Alarm>()

        override suspend fun getAlarmById(id: Long): Alarm? {
            return alarms[id]
        }

        override suspend fun getAlarmsByGroupId(groupId: Long): List<Alarm> {
            return alarms.values.filter { it.groupId == groupId }
        }

        override suspend fun upsertAlarm(alarm: Alarm): Alarm {
            alarms[alarm.id] = alarm
            return alarm
        }

        override suspend fun deleteAlarm(alarm: Alarm) {
            deletedAlarms.add(alarm)
            alarms.remove(alarm.id)
        }
    }

    private class FakeAlarmScheduler : AlarmScheduler {
        val scheduledAlarms = mutableListOf<Pair<Set<DayOfWeek>, Alarm>>()
        val canceledAlarms = mutableListOf<Pair<Set<DayOfWeek>, Alarm>>()
        val canceledGroupIds = mutableListOf<Long>()

        override suspend fun schedule(repeatDays: Set<DayOfWeek>, alarm: Alarm) {
            scheduledAlarms.add(repeatDays to alarm)
        }

        override fun cancel(repeatDays: Set<DayOfWeek>, alarm: Alarm) {
            canceledAlarms.add(repeatDays to alarm)
        }

        override fun cancelByGroup(groupId: Long) {
            canceledGroupIds.add(groupId)
        }

        override fun scheduleTimer(triggerTime: Long) {
            // No-op for testing
        }

        override fun cancelTimer() {
            // No-op for testing
        }
    }
}