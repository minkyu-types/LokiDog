package dev.loki.dog.model

import dev.loki.dog.feature.detailalarmgroup.TempIdGenerator

data class AlarmModel(
    val id: Long,
    val groupId: Long?,
    val time: Long, // 언제 울려야하는지
    val isActivated: Boolean,
    val isTemp: Boolean,
) {
    companion object {
        fun createTemp(groupId: Long): AlarmModel = AlarmModel(
            id = TempIdGenerator.next(),
            groupId = groupId,
            time = 0,
            isActivated = true,
            isTemp = true,
        )
    }
}