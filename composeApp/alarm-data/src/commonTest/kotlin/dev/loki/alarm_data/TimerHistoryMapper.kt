package dev.loki.alarm_data

import dev.loki.alarm_data.mapper.TimerHistoryMapper
import dev.loki.alarm_data.model.TimerHistoryEntity
import dev.loki.timerhistory.model.TimerHistory
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TimerHistoryMapper {

    @Test
    fun `Domain 타이머 기록을 Data 타이머 기록으로 변환해야 한다`() = runTest {
        // Given
        val mapper = TimerHistoryMapper()
        val domainTimerHistory = TimerHistory(
            id = 1111L,
            durationTimeMillis = 10000L,
        )

        // When
        val dataTimerHistory = mapper.mapToData(domainTimerHistory)

        // Then
        assertEquals(dataTimerHistory.id, domainTimerHistory.id)
        assertEquals(dataTimerHistory.durationTimeMillis, domainTimerHistory.durationTimeMillis)
    }

    @Test
    fun `Data 타이머 기록을 Domain 타이머 기록으로 변환해야 한다`() = runTest {
        // Given
        val mapper = TimerHistoryMapper()
        val dataTimerHistory = TimerHistoryEntity(
            id = 1111L,
            durationTimeMillis = 10000L,
        )

        // When
        val domainTimerHistory = mapper.mapToDomain(dataTimerHistory)

        // Then
        assertEquals(dataTimerHistory.id, domainTimerHistory.id)
        assertEquals(dataTimerHistory.durationTimeMillis, domainTimerHistory.durationTimeMillis)
    }
}