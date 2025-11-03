package dev.loki.alarm.usecase.alarm

import dev.loki.alarm.FakeAlarmRepository
import dev.loki.alarm.model.Alarm
import dev.loki.alarm.usecase.UpsertAlarmUseCase
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpsertAlarmUseCaseTest {

    @Test
    fun `제공된 알람으로 repository의 upsertAlarm을 호출해야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val useCase = UpsertAlarmUseCase(mockRepository)

        val testAlarm = Alarm(
            id = 1L,
            groupId = 100L,
            time = "09:00",
            memo = "Morning Alarm",
            isActivated = true,
            isTemp = false
        )
        val repeatDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)

        // When
        useCase.invoke(testAlarm)

        // Then
        assertTrue(mockRepository.upsertedAlarms.contains(testAlarm))
        assertEquals(1, mockRepository.upsertedAlarms.size)
    }

    @Test
    fun `반복 요일이 없는 알람을 처리할 수 있어야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val useCase = UpsertAlarmUseCase(mockRepository)

        val testAlarm = Alarm(
            id = 2L,
            groupId = 200L,
            time = "14:30",
            memo = "Afternoon Reminder",
            isActivated = false,
            isTemp = false
        )
        val emptyRepeatDays = emptySet<DayOfWeek>()

        // When
        useCase.invoke(testAlarm)

        // Then
        assertTrue(mockRepository.upsertedAlarms.contains(testAlarm))
    }

    @Test
    fun `임시 알람을 처리할 수 있어야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val useCase = UpsertAlarmUseCase(mockRepository)

        val tempAlarm = Alarm(
            id = 0L,
            groupId = 0L,
            time = "20:00",
            memo = "Temp Alarm",
            isActivated = true,
            isTemp = true
        )
        val repeatDays = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

        // When
        useCase.invoke(tempAlarm)

        // Then
        assertEquals(1, mockRepository.upsertedAlarms.size)
        assertTrue(mockRepository.upsertedAlarms.first().isTemp)
    }

    @Test
    fun `모든 요일이 설정된 알람을 처리할 수 있어야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val useCase = UpsertAlarmUseCase(mockRepository)

        val dailyAlarm = Alarm(
            id = 3L,
            groupId = 300L,
            time = "07:00",
            memo = "Daily Alarm",
            isActivated = true,
            isTemp = false
        )
        val allDays = DayOfWeek.entries.toSet()

        // When
        useCase.invoke(dailyAlarm)

        // Then
        assertTrue(mockRepository.upsertedAlarms.contains(dailyAlarm))
    }

    @Test
    fun `여러 알람을 순차적으로 처리할 수 있어야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val useCase = UpsertAlarmUseCase(mockRepository)

        val alarm1 = Alarm(1L, 100L, "08:00", "First", true, false)
        val alarm2 = Alarm(2L, 100L, "09:00", "Second", true, false)
        val alarm3 = Alarm(3L, 200L, "10:00", "Third", false, false)

        val repeatDays = setOf(DayOfWeek.MONDAY)

        // When
        useCase.invoke(alarm1)
        useCase.invoke(alarm2)
        useCase.invoke(alarm3)

        // Then
        assertEquals(3, mockRepository.upsertedAlarms.size)
        assertTrue(mockRepository.upsertedAlarms.containsAll(listOf(alarm1, alarm2, alarm3)))
    }

    @Test
    fun `기존 알람을 업데이트할 수 있어야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val useCase = UpsertAlarmUseCase(mockRepository)

        val originalAlarm = Alarm(1L, 100L, "08:00", "Original", true, false)
        val updatedAlarm = Alarm(1L, 100L, "08:30", "Updated", true, false)
        val repeatDays = setOf(DayOfWeek.TUESDAY)

        // When
        useCase.invoke(originalAlarm)
        useCase.invoke(updatedAlarm)

        // Then
        assertEquals(2, mockRepository.upsertedAlarms.size)
        assertTrue(mockRepository.upsertedAlarms.contains(updatedAlarm))
    }
}