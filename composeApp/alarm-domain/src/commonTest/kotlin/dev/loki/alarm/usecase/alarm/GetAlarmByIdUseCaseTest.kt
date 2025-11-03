package dev.loki.alarm.usecase.alarm

import dev.loki.alarm.FakeAlarmRepository
import dev.loki.alarm.model.Alarm
import dev.loki.alarm.usecase.GetAlarmByIdUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GetAlarmByIdUseCaseTest {

    @Test
    fun `존재하는 알람 ID로 알람을 조회할 수 있어야 한다`() = runTest {
        // Given: 저장소에 알람이 저장되어 있고
        val mockRepository = FakeAlarmRepository()
        val useCase = GetAlarmByIdUseCase(mockRepository)

        val testAlarm = Alarm(
            id = 1L,
            groupId = 100L,
            time = "09:00",
            memo = "Morning Alarm",
            isActivated = true,
            isTemp = false
        )
        mockRepository.addAlarm(testAlarm)

        // When: 해당 ID로 알람을 조회하면
        val result = useCase.invoke(1L)

        // Then: 저장된 알람이 반환되어야 한다
        assertNotNull(result)
        assertEquals(testAlarm.id, result.id)
        assertEquals(testAlarm.groupId, result.groupId)
        assertEquals(testAlarm.time, result.time)
        assertEquals(testAlarm.memo, result.memo)
        assertEquals(testAlarm.isActivated, result.isActivated)
        assertEquals(testAlarm.isTemp, result.isTemp)
    }

    @Test
    fun `존재하지 않는 알람 ID로 조회하면 null을 반환해야 한다`() = runTest {
        // Given: 저장소가 비어있고
        val mockRepository = FakeAlarmRepository()
        val useCase = GetAlarmByIdUseCase(mockRepository)

        // When: 존재하지 않는 ID로 알람을 조회하면
        val result = useCase.invoke(999L)

        // Then: null이 반환되어야 한다
        assertNull(result)
    }

    @Test
    fun `여러 알람 중 특정 ID의 알람만 조회할 수 있어야 한다`() = runTest {
        // Given: 저장소에 여러 알람이 저장되어 있고
        val mockRepository = FakeAlarmRepository()
        val useCase = GetAlarmByIdUseCase(mockRepository)

        val alarm1 = Alarm(1L, 100L, "08:00", "First", true, false)
        val alarm2 = Alarm(2L, 100L, "09:00", "Second", true, false)
        val alarm3 = Alarm(3L, 200L, "10:00", "Third", false, false)

        mockRepository.addAlarm(alarm1)
        mockRepository.addAlarm(alarm2)
        mockRepository.addAlarm(alarm3)

        // When: 특정 ID로 알람을 조회하면
        val result = useCase.invoke(2L)

        // Then: 해당 알람만 반환되어야 한다
        assertNotNull(result)
        assertEquals(alarm2.id, result.id)
        assertEquals(alarm2.memo, result.memo)
    }

    @Test
    fun `활성화되지 않은 알람도 조회할 수 있어야 한다`() = runTest {
        // Given: 저장소에 비활성화된 알람이 저장되어 있고
        val mockRepository = FakeAlarmRepository()
        val useCase = GetAlarmByIdUseCase(mockRepository)

        val inactiveAlarm = Alarm(
            id = 5L,
            groupId = 200L,
            time = "14:30",
            memo = "Inactive Alarm",
            isActivated = false,
            isTemp = false
        )
        mockRepository.addAlarm(inactiveAlarm)

        // When: 해당 ID로 알람을 조회하면
        val result = useCase.invoke(5L)

        // Then: 비활성화된 알람도 정상적으로 반환되어야 한다
        assertNotNull(result)
        assertEquals(false, result.isActivated)
    }

    @Test
    fun `임시 알람도 조회할 수 있어야 한다`() = runTest {
        // Given: 저장소에 임시 알람이 저장되어 있고
        val mockRepository = FakeAlarmRepository()
        val useCase = GetAlarmByIdUseCase(mockRepository)

        val tempAlarm = Alarm(
            id = 0L,
            groupId = 0L,
            time = "20:00",
            memo = "Temp Alarm",
            isActivated = true,
            isTemp = true
        )
        mockRepository.addAlarm(tempAlarm)

        // When: 해당 ID로 알람을 조회하면
        val result = useCase.invoke(0L)

        // Then: 임시 알람도 정상적으로 반환되어야 한다
        assertNotNull(result)
        assertEquals(true, result.isTemp)
    }

    @Test
    fun `같은 그룹의 다른 알람과 구분하여 조회할 수 있어야 한다`() = runTest {
        // Given: 저장소에 같은 그룹의 여러 알람이 저장되어 있고
        val mockRepository = FakeAlarmRepository()
        val useCase = GetAlarmByIdUseCase(mockRepository)

        val groupId = 100L
        val alarm1 = Alarm(1L, groupId, "08:00", "First in group", true, false)
        val alarm2 = Alarm(2L, groupId, "09:00", "Second in group", true, false)
        val alarm3 = Alarm(3L, groupId, "10:00", "Third in group", true, false)

        mockRepository.addAlarm(alarm1)
        mockRepository.addAlarm(alarm2)
        mockRepository.addAlarm(alarm3)

        // When: 특정 ID로 알람을 조회하면
        val result = useCase.invoke(2L)

        // Then: 같은 그룹이더라도 정확한 알람만 반환되어야 한다
        assertNotNull(result)
        assertEquals(2L, result.id)
        assertEquals("Second in group", result.memo)
        assertEquals(groupId, result.groupId)
    }

    @Test
    fun `큰 ID 값을 가진 알람도 조회할 수 있어야 한다`() = runTest {
        // Given: 저장소에 큰 ID 값을 가진 알람이 저장되어 있고
        val mockRepository = FakeAlarmRepository()
        val useCase = GetAlarmByIdUseCase(mockRepository)

        val largeIdAlarm = Alarm(
            id = 999999L,
            groupId = 100L,
            time = "06:00",
            memo = "Large ID Alarm",
            isActivated = true,
            isTemp = false
        )
        mockRepository.addAlarm(largeIdAlarm)

        // When: 해당 큰 ID로 알람을 조회하면
        val result = useCase.invoke(999999L)

        // Then: 정상적으로 알람이 반환되어야 한다
        assertNotNull(result)
        assertEquals(999999L, result.id)
    }
}