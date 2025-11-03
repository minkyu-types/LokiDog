package dev.loki.alarm.usecase.alarm

import dev.loki.alarm.FakeAlarmRepository
import dev.loki.alarm.generateAlarm
import dev.loki.alarm.model.Alarm
import dev.loki.alarm.usecase.GetAlarmsByGroupIdUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GetAlarmsByGroupIdUseCaseTest {

    @Test
    fun `존재하는 groupId로 알림 리스트를 조회할 수 있어야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val useCase = GetAlarmsByGroupIdUseCase(mockRepository)

        val groupId = 1234L
        (1..5).map {
            generateAlarm(it.toLong(), groupId)
        }.forEach {
            mockRepository.addAlarm(it)
        }

        // When
        val result = useCase.invoke(groupId)

        // Then
        assertNotNull(result)
        assertTrue {
            result.all { it.groupId == groupId }
                    && result.size == 5
        }
    }

    @Test
    fun `존재하지 않는 groupId로 조회할 경우 빈 리스트를 반환해야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val useCase = GetAlarmsByGroupIdUseCase(mockRepository)

        // When
        val groupId = 9999L
        (1..5).map {
            generateAlarm(it.toLong(), 1111L)
        }.forEach {
            mockRepository.addAlarm(it)
        }
        val result = useCase.invoke(groupId)

        // Then
        assertNotNull(result)
        assertTrue(result.isEmpty())
    }

    @Test
    fun `groupId가 0L인 경우 빈 리스트를 반환해야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val useCase = GetAlarmsByGroupIdUseCase(mockRepository)

        // When
        val groupId = 0L
        (1..5).map {
            generateAlarm(it.toLong(), 9999L)
        }.forEach {
            mockRepository.addAlarm(it)
        }
        val result = useCase.invoke(groupId)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `조회된 알람들의 groupId가 모두 요청한 groupId와 일치해야 한다`() = runTest {
        // Given
        val mockRepository = FakeAlarmRepository()
        val useCase = GetAlarmsByGroupIdUseCase(mockRepository)

        // When
        val groupId = 9999L
        (1..5).map {
            generateAlarm(it.toLong(), groupId)
        }.forEach {
            mockRepository.addAlarm(it)
        }
        val result = useCase.invoke(groupId)

        // Then
        assertNotNull(result)
        assertTrue {
            result.all { it.groupId == groupId }
        }
    }
}