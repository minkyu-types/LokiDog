package dev.loki.alarm.usecase.alarmgroup

import dev.loki.alarm.FakeAlarmGroupRepository
import dev.loki.alarm.generateAlarmGroup
import dev.loki.alarmgroup.usecase.InsertAlarmGroupUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class InsertAlarmGroupUseCaseTest {

    @Test
    fun `알람 그룹을 저장하고 생성된 ID를 반환해야 한다`() = runTest {
        // Given: 저장소와 usecase가 준비되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = InsertAlarmGroupUseCase(repository)

        val alarmGroup = generateAlarmGroup(id = 0L, isTemp = false)

        // When: 알람 그룹을 저장하면
        val createdId = useCase(alarmGroup)

        // Then: 생성된 ID를 반환하고 저장소에서 조회 가능해야 한다
        assertNotEquals(0L, createdId)
        val saved = repository.getAlarmGroupById(createdId)
        assertNotNull(saved)
        assertEquals(createdId, saved.id)
    }

    @Test
    fun `ID가 0인 알람 그룹을 저장하면 새로운 ID가 생성되어야 한다`() = runTest {
        // Given: 저장소와 usecase가 준비되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = InsertAlarmGroupUseCase(repository)

        val alarmGroup = generateAlarmGroup(id = 0L, isTemp = false)

        // When: ID가 0인 알람 그룹을 저장하면
        val createdId = useCase(alarmGroup)

        // Then: 0이 아닌 새로운 ID가 생성되어야 한다
        assertNotEquals(0L, createdId)
    }

    @Test
    fun `여러 알람 그룹을 저장하면 각각 고유한 ID가 생성되어야 한다`() = runTest {
        // Given: 저장소와 usecase가 준비되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = InsertAlarmGroupUseCase(repository)

        val group1 = generateAlarmGroup(id = 0L, isTemp = false)
        val group2 = generateAlarmGroup(id = 0L, isTemp = false)
        val group3 = generateAlarmGroup(id = 0L, isTemp = false)

        // When: 여러 알람 그룹을 저장하면
        val id1 = useCase(group1)
        val id2 = useCase(group2)
        val id3 = useCase(group3)

        // Then: 각각 고유한 ID를 가져야 한다
        assertNotEquals(id1, id2)
        assertNotEquals(id2, id3)
        assertNotEquals(id1, id3)

        // 모든 그룹이 저장소에서 조회 가능해야 한다
        assertNotNull(repository.getAlarmGroupById(id1))
        assertNotNull(repository.getAlarmGroupById(id2))
        assertNotNull(repository.getAlarmGroupById(id3))
    }

    @Test
    fun `임시 알람 그룹을 저장할 수 있어야 한다`() = runTest {
        // Given: 저장소와 usecase가 준비되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = InsertAlarmGroupUseCase(repository)

        val tempGroup = generateAlarmGroup(id = 0L, isTemp = true)

        // When: 임시 알람 그룹을 저장하면
        val createdId = useCase(tempGroup)

        // Then: 저장소에서 임시 알람 그룹으로 조회 가능해야 한다
        val saved = repository.getAlarmGroupById(createdId)
        assertNotNull(saved)
        assertEquals(true, saved.isTemp)
    }

    @Test
    fun `활성화된 알람 그룹을 저장할 수 있어야 한다`() = runTest {
        // Given: 저장소와 usecase가 준비되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = InsertAlarmGroupUseCase(repository)

        val activeGroup = generateAlarmGroup(id = 0L, isActivate = true, isTemp = false)

        // When: 활성화된 알람 그룹을 저장하면
        val createdId = useCase(activeGroup)

        // Then: 활성화 상태가 유지되어야 한다
        val saved = repository.getAlarmGroupById(createdId)
        assertNotNull(saved)
        assertEquals(true, saved.isActivated)
    }

    @Test
    fun `비활성화된 알람 그룹을 저장할 수 있어야 한다`() = runTest {
        // Given: 저장소와 usecase가 준비되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = InsertAlarmGroupUseCase(repository)

        val inactiveGroup = generateAlarmGroup(id = 0L, isActivate = false, isTemp = false)

        // When: 비활성화된 알람 그룹을 저장하면
        val createdId = useCase(inactiveGroup)

        // Then: 비활성화 상태가 유지되어야 한다
        val saved = repository.getAlarmGroupById(createdId)
        assertNotNull(saved)
        assertEquals(false, saved.isActivated)
    }

    @Test
    fun `알람 그룹의 모든 속성이 올바르게 저장되어야 한다`() = runTest {
        // Given: 저장소와 usecase가 준비되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = InsertAlarmGroupUseCase(repository)

        val alarmGroup = generateAlarmGroup(id = 0L, isTemp = false).copy(
            title = "Morning Routine",
            description = "Wake up alarms",
            includedAlarmsSize = 5,
            order = 3
        )

        // When: 알람 그룹을 저장하면
        val createdId = useCase(alarmGroup)

        // Then: 모든 속성이 올바르게 저장되어야 한다
        val saved = repository.getAlarmGroupById(createdId)
        assertNotNull(saved)
        assertEquals("Morning Routine", saved.title)
        assertEquals("Wake up alarms", saved.description)
        assertEquals(5, saved.includedAlarmsSize)
        assertEquals(3, saved.order)
    }

    @Test
    fun `특정 ID를 가진 알람 그룹을 저장하면 해당 ID로 저장되어야 한다`() = runTest {
        // Given: 저장소와 usecase가 준비되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = InsertAlarmGroupUseCase(repository)

        val specificId = 999L
        val alarmGroup = generateAlarmGroup(id = specificId, isTemp = false)

        // When: 특정 ID를 가진 알람 그룹을 저장하면
        val createdId = useCase(alarmGroup)

        // Then: 지정한 ID로 저장되어야 한다
        assertEquals(specificId, createdId)
        val saved = repository.getAlarmGroupById(specificId)
        assertNotNull(saved)
    }

    @Test
    fun `빈 제목과 설명을 가진 알람 그룹도 저장할 수 있어야 한다`() = runTest {
        // Given: 저장소와 usecase가 준비되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = InsertAlarmGroupUseCase(repository)

        val alarmGroup = generateAlarmGroup(id = 0L, isTemp = false).copy(
            title = "",
            description = ""
        )

        // When: 빈 제목과 설명을 가진 알람 그룹을 저장하면
        val createdId = useCase(alarmGroup)

        // Then: 정상적으로 저장되어야 한다
        val saved = repository.getAlarmGroupById(createdId)
        assertNotNull(saved)
        assertEquals("", saved.title)
        assertEquals("", saved.description)
    }

    @Test
    fun `순차적으로 여러 알람 그룹을 저장하면 증가하는 ID를 받아야 한다`() = runTest {
        // Given: 저장소와 usecase가 준비되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = InsertAlarmGroupUseCase(repository)

        val ids = mutableListOf<Long>()

        // When: 순차적으로 5개의 알람 그룹을 저장하면
        repeat(5) {
            val group = generateAlarmGroup(id = 0L, isTemp = false)
            val createdId = useCase(group)
            ids.add(createdId)
        }

        // Then: ID가 순차적으로 증가해야 한다
        assertEquals(5, ids.size)
        assertEquals(ids.sorted(), ids) // ID가 정렬된 순서와 동일해야 함
    }

    @Test
    fun `저장된 알람 그룹은 즉시 조회 가능해야 한다`() = runTest {
        // Given: 저장소와 usecase가 준비되어 있고
        val repository = FakeAlarmGroupRepository()
        val useCase = InsertAlarmGroupUseCase(repository)

        val alarmGroup = generateAlarmGroup(id = 0L, isTemp = false)

        // When: 알람 그룹을 저장한 직후
        val createdId = useCase(alarmGroup)

        // Then: 즉시 조회 가능해야 한다
        val saved = repository.getAlarmGroupById(createdId)
        assertNotNull(saved)
        assertEquals(createdId, saved.id)
    }
}