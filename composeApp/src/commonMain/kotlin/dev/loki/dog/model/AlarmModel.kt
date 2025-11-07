package dev.loki.dog.model

import androidx.compose.runtime.Immutable
import dev.loki.dog.feature.addalarmgroup.TempAlarmTimeGenerator

@Immutable
data class AlarmModel(
    val id: Long,
    val groupId: Long,
    val time: String,
    val memo: String,
    val isActivated: Boolean,
    val isTemp: Boolean,
) {
    companion object {
        fun createTemp(groupId: Long, latestAlarm: AlarmModel?): AlarmModel {
            if (latestAlarm == null) {
                return AlarmModel(
                    id = 0,
                    groupId = groupId,
                    time = "00:05",
                    memo = "",
                    isActivated = true,
                    isTemp = true,
                )
            }

            val prevTimes = latestAlarm.time
            val (latestHour,latestMinute)=  prevTimes.split(":").map { it.toInt() }
            var newTime = TempAlarmTimeGenerator.nextTime(latestHour, latestMinute)

            while (newTime in prevTimes) {
                val (hour, minute) = newTime.split(":").map { it.toInt() }
                newTime = TempAlarmTimeGenerator.nextTime(hour, minute)
            }

            return AlarmModel(
                id = 0,
                groupId = groupId,
                time = newTime,
                memo = "",
                isActivated = true,
                isTemp = true,
            )
        }
    }
}