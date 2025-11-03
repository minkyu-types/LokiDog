package dev.loki.alarm.usecase.alarmgroup

import dev.loki.alarm.FakeAlarmGroupRepository
import dev.loki.alarm.generateAlarmGroup
import dev.loki.alarmgroup.model.AlarmGroup
import dev.loki.alarmgroup.usecase.GetAlarmGroupByIdUseCase
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.DayOfWeek
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GetAlarmGroupByIdUseCaseTest {

    @Test
    fun `존재하는 알람 그룹 ID로 알람 그룹을 조회할 수 있어야 한다`() = runTest {
        // Given: 저장소에 알람 그룹이 저장되어 있고
        val mockRepository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupByIdUseCase(mockRepository)

        val testGroup = AlarmGroup(
            id = 1L,
            order = 0,
            title = "Morning Routine",
            description = "Wake up alarms",
            includedAlarmsSize = 3,
            repeatDays = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
            isActivated = true,
            created = 1623456789L,
            updated = 1623456789L,
            isTemp = false
        )
        mockRepository.addAlarmGroup(testGroup)

        // When: 해당 ID로 알람 그룹을 조회하면
        val result = useCase.invoke(1L)

        // Then: 저장된 알람 그룹이 반환되어야 한다
        assertNotNull(result)
        assertEquals(testGroup.id, result.id)
        assertEquals(testGroup.title, result.title)
        assertEquals(testGroup.description, result.description)
        assertEquals(testGroup.includedAlarmsSize, result.includedAlarmsSize)
        assertEquals(testGroup.repeatDays, result.repeatDays)
        assertEquals(testGroup.isActivated, result.isActivated)
        assertEquals(testGroup.isTemp, result.isTemp)
    }

    @Test
    fun `존재하지 않는 알람 그룹 ID로 조회하면 null을 반환해야 한다`() = runTest {
        // Given: 저장소가 비어있고
        val mockRepository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupByIdUseCase(mockRepository)

        // When: 존재하지 않는 ID로 알람 그룹을 조회하면
        val result = useCase.invoke(999L)

        // Then: null이 반환되어야 한다
        assertNull(result)
    }

    @Test
    fun `여러 알람 그룹 중 특정 ID의 알람 그룹만 조회할 수 있어야 한다`() = runTest {
        // Given: 저장소에 여러 알람 그룹이 저장되어 있고
        val mockRepository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupByIdUseCase(mockRepository)

        val group1 = generateAlarmGroup(id = 1L)
        val group2 = generateAlarmGroup(id = 2L)
        val group3 = generateAlarmGroup(id = 3L)

        mockRepository.addAlarmGroup(group1)
        mockRepository.addAlarmGroup(group2)
        mockRepository.addAlarmGroup(group3)

        // When: 특정 ID로 알람 그룹을 조회하면
        val result = useCase.invoke(2L)

        // Then: 해당 알람 그룹만 반환되어야 한다
        assertNotNull(result)
        assertEquals(2L, result.id)
    }

    @Test
    fun `활성화되지 않은 알람 그룹도 조회할 수 있어야 한다`() = runTest {
        // Given: 저장소에 비활성화된 알람 그룹이 저장되어 있고
        val mockRepository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupByIdUseCase(mockRepository)

        val inactiveGroup = generateAlarmGroup(id = 5L, isActivate = false)
        mockRepository.addAlarmGroup(inactiveGroup)

        // When: 해당 ID로 알람 그룹을 조회하면
        val result = useCase.invoke(5L)

        // Then: 비활성화된 알람 그룹도 정상적으로 반환되어야 한다
        assertNotNull(result)
        assertEquals(false, result.isActivated)
    }

    @Test
    fun `임시 알람 그룹도 조회할 수 있어야 한다`() = runTest {
        // Given: 저장소에 임시 알람 그룹이 저장되어 있고
        val mockRepository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupByIdUseCase(mockRepository)

        val tempGroup = generateAlarmGroup(id = 10L, isTemp = true)
        mockRepository.addAlarmGroup(tempGroup)

        // When: 해당 ID로 알람 그룹을 조회하면
        val result = useCase.invoke(10L)

        // Then: 임시 알람 그룹도 정상적으로 반환되어야 한다
        assertNotNull(result)
        assertEquals(true, result.isTemp)
    }

    @Test
    fun `반복 요일이 없는 알람 그룹도 조회할 수 있어야 한다`() = runTest {
        // Given: 저장소에 반복 요일이 없는 알람 그룹이 저장되어 있고
        val mockRepository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupByIdUseCase(mockRepository)

        val noRepeatGroup = AlarmGroup(
            id = 15L,
            order = 0,
            title = "One-time Alarm",
            description = "No repeat",
            includedAlarmsSize = 1,
            repeatDays = emptySet(),
            isActivated = true,
            created = 1623456789L,
            updated = 1623456789L,
            isTemp = false
        )
        mockRepository.addAlarmGroup(noRepeatGroup)

        // When: 해당 ID로 알람 그룹을 조회하면
        val result = useCase.invoke(15L)

        // Then: 반복 요일이 없는 알람 그룹도 정상적으로 반환되어야 한다
        assertNotNull(result)
        assertEquals(emptySet(), result.repeatDays)
    }

    @Test
    fun `모든 요일이 설정된 알람 그룹도 조회할 수 있어야 한다`() = runTest {
        // Given: 저장소에 모든 요일이 설정된 알람 그룹이 저장되어 있고
        val mockRepository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupByIdUseCase(mockRepository)

        val allDaysGroup = AlarmGroup(
            id = 20L,
            order = 0,
            title = "Daily Alarm",
            description = "Every day",
            includedAlarmsSize = 7,
            repeatDays = DayOfWeek.entries.toSet(),
            isActivated = true,
            created = 1623456789L,
            updated = 1623456789L,
            isTemp = false
        )
        mockRepository.addAlarmGroup(allDaysGroup)

        // When: 해당 ID로 알람 그룹을 조회하면
        val result = useCase.invoke(20L)

        // Then: 모든 요일이 설정된 알람 그룹도 정상적으로 반환되어야 한다
        assertNotNull(result)
        assertEquals(7, result.repeatDays.size)
        assertEquals(DayOfWeek.entries.toSet(), result.repeatDays)
    }

    @Test
    fun `큰 ID 값을 가진 알람 그룹도 조회할 수 있어야 한다`() = runTest {
        // Given: 저장소에 큰 ID 값을 가진 알람 그룹이 저장되어 있고
        val mockRepository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupByIdUseCase(mockRepository)

        val largeIdGroup = generateAlarmGroup(id = 999999L)
        mockRepository.addAlarmGroup(largeIdGroup)

        // When: 해당 큰 ID로 알람 그룹을 조회하면
        val result = useCase.invoke(999999L)

        // Then: 정상적으로 알람 그룹이 반환되어야 한다
        assertNotNull(result)
        assertEquals(999999L, result.id)
    }

    @Test
    fun `같은 ID로 여러 번 조회해도 동일한 결과를 반환해야 한다`() = runTest {
        // Given: 저장소에 알람 그룹이 저장되어 있고
        val mockRepository = FakeAlarmGroupRepository()
        val useCase = GetAlarmGroupByIdUseCase(mockRepository)

        val testGroup = generateAlarmGroup(id = 100L)
        mockRepository.addAlarmGroup(testGroup)

        // When: 같은 ID로 여러 번 조회하면
        val result1 = useCase.invoke(100L)
        val result2 = useCase.invoke(100L)
        val result3 = useCase.invoke(100L)

        // Then: 모두 동일한 알람 그룹을 반환해야 한다
        assertNotNull(result1)
        assertNotNull(result2)
        assertNotNull(result3)
        assertEquals(result1.id, result2.id)
        assertEquals(result2.id, result3.id)
        assertEquals(result1.title, result2.title)
    }
}