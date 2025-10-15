package dev.loki.dog.model

import dev.loki.dog.feature.addalarmgroup.TempAlarmTimeGenerator

data class AlarmModel(
    val id: Long,
    val groupId: Long,
    val time: String,
    val isActivated: Boolean,
    val isTemp: Boolean,
) {
    companion object {
        fun createTemp(groupId: Long, prevAlarms: List<AlarmModel>): AlarmModel {
            var newTime = TempAlarmTimeGenerator.nextTime()
            val prevTimes = prevAlarms.map { it.time }

            while (newTime in prevTimes) {
                newTime = TempAlarmTimeGenerator.nextTime()
            }

            return AlarmModel(
                id = 0,
                groupId = groupId,
                time = newTime,
                isActivated = true,
                isTemp = true,
            )
        }
    }
}