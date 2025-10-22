package dev.loki.dog.util

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun getAlarmTime(repeatDays: Set<DayOfWeek>, time: String): Long {
    val (hour, minute) = try {
        time.split(":").map { it.toInt() }
    } catch (e: NumberFormatException) {
        e.printStackTrace()
        throw e
    }

    val now = Clock.System.now()
    val timezone = TimeZone.currentSystemDefault()
    val currentDateTime = now.toLocalDateTime(timezone)
    val today = now.toLocalDateTime(timezone).date
    val currentDayOfWeek = today.dayOfWeek

    var targetDateTime = LocalDateTime(
        year = today.year,
        month = today.month,
        day = today.day,
        hour = hour,
        minute = minute
    )

    val nextDayOffset = findNextRepeatDayOfWeekOffset(
        currentDayOfWeek = currentDayOfWeek,
        repeatDays = repeatDays,
        targetDateTime = targetDateTime,
        currentDateTime = currentDateTime
    )

    targetDateTime = targetDateTime.plusDays(DatePeriod(days = nextDayOffset))

    return targetDateTime.toInstant(timezone).toEpochMilliseconds()
}

private fun findNextRepeatDayOfWeekOffset(
    currentDayOfWeek: DayOfWeek,
    repeatDays: Set<DayOfWeek>,
    targetDateTime: LocalDateTime,
    currentDateTime: LocalDateTime
): Int {
    if (repeatDays.isEmpty()) return 0

    for (i in 0..6) {
        val candidate = DayOfWeek.entries[(currentDayOfWeek.ordinal + i) % DayOfWeek.entries.size]
        val isRepeatDay = candidate in repeatDays // 반복할 요일이 있는지
        val isToday = (i == 0) // 오늘이 반복할 요일인지
        val isFutureTime = (targetDateTime > currentDateTime) // 다음 요일로 바꿔야 하는지

        if (isRepeatDay && (!isToday || isFutureTime)) {
            return i
        }
    }

    return 7
}

fun LocalDateTime.plusDays(datePeriod: DatePeriod): LocalDateTime {
    val date = this.date.plus(DatePeriod(days = datePeriod.days))
    return LocalDateTime(
        year = date.year,
        month = date.month,
        day = date.day,
        hour = this.hour,
        minute = this.minute,
        second = this.second,
        nanosecond = this.nanosecond
    )
}