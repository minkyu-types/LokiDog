package dev.loki.alarm_data

import dev.loki.alarm_data.mapper.AlarmGroupMapper
import dev.loki.alarmgroup.model.AlarmGroup
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AlarmGroupMapperTest {

    @Test
    fun `Domain 알람 그룹을 Data 알람 그룹으로 변환해야 한다`() = runTest {
        // Given
        val domainAlarmGroup = AlarmGroup(
            id = 1111L,
            order = 0,
            title = "Test Group",
            description = "This is a test group",
            repeatDays = emptySet(),
            includedAlarmsSize = 5,
            created = 1623456789L,
            updated = 1623456789L,
            isActivated = true,
            isTemp = true
        )
        val mapper = AlarmGroupMapper()

        // When
        val dataAlarmGroup = mapper.mapToData(domainAlarmGroup)

        // Then
        assertEquals(dataAlarmGroup.id, domainAlarmGroup.id)
        assertEquals(dataAlarmGroup.repeatDays, domainAlarmGroup.repeatDays)
    }

    @Test
    fun `Data 알람 그룹을 Domain 알람 그룹으로 변환해야 한다`() = runTest {
        // Given
        val dataAlarmGroup = generateAlarmGroup(id = 1111L)
        val mapper = AlarmGroupMapper()

        // When
        val domainAlarmGroup = mapper.mapToDomain(dataAlarmGroup)

        // Then
        assertEquals(domainAlarmGroup.id, dataAlarmGroup.id)
        assertEquals(domainAlarmGroup.repeatDays, dataAlarmGroup.repeatDays)
    }
}