package dev.loki.alarm_data

import dev.loki.alarm.model.Alarm
import dev.loki.alarm_data.mapper.AlarmMapper
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AlarmMapperTest {

    @Test
    fun `Data 계층의 데이터를 Domain 계층의 데이터로 변환해야 한다`() = runTest {
        // Given
        val dataAlarm = generateAlarm(id = 1111L, groupId = 9999L)
        val alarmMapper = AlarmMapper()

        // When
        val domainAlarm = alarmMapper.mapToDomain(dataAlarm)

        // Then
        assertEquals(dataAlarm.id, domainAlarm.id)
        assertEquals(dataAlarm.groupId, domainAlarm.groupId)
    }

    @Test
    fun `Domain 계층의 데이터를 Data 계층의 데이터로 변환해야 한다`() = runTest {
        // Given
        val domainAlarm = Alarm(
            id = 1111L,
            groupId = 9999L,
            time = "09:00",
            memo = "Morning Alarm",
            isActivated = true,
            isTemp = true
        )
        val alarmMapper = AlarmMapper()

        // When
        val dataAlarm = alarmMapper.mapToData(domainAlarm)

        // Then
        assertEquals(domainAlarm.id, dataAlarm.id)
        assertEquals(domainAlarm.groupId, dataAlarm.groupId)
    }
}