package dev.loki.dog.expect

import dev.loki.AlarmScheduler
import dev.loki.alarm.model.Alarm
import kotlinx.datetime.DayOfWeek

actual class PlatformAlarmScheduler(

): AlarmScheduler {

    override suspend fun schedule(repeatDays: Set<DayOfWeek>, alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override suspend fun cancel(alarmId: Long) {
        TODO("Not yet implemented")
    }
}