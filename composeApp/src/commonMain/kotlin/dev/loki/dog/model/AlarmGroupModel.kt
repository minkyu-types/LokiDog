package dev.loki.dog.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class AlarmGroupModel(
    val order: Int,
    val id: Long,
    val title: String,
    val repeatDays: Set<DayOfWeek> = emptySet(),
    val alarmSize: Int,
    val description: String,
    val created: Long,
    val updated: Long,
    val isActivated: Boolean,
    val isTemp : Boolean,
) {

    companion object {
        @OptIn(ExperimentalTime::class)
        fun createTemp(): AlarmGroupModel {
            val todayOfWeek = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek
            return AlarmGroupModel(
                order = 0,
                id = 0,
                title = "",
                repeatDays = setOf(todayOfWeek),
                alarmSize = 0,
                description = "",
                created = 0,
                updated = 0,
                isActivated = true,
                isTemp = true,
            )
        }
    }
}