package dev.loki.alarm

import dev.loki.AlarmScheduler
import dev.loki.alarm.model.Alarm
import kotlinx.datetime.DayOfWeek

class FakeAlarmScheduler : AlarmScheduler {
    val scheduledAlarms = mutableListOf<Pair<Set<DayOfWeek>, Alarm>>()
    val canceledAlarms = mutableListOf<Pair<Set<DayOfWeek>, Alarm>>()
    val canceledGroupIds = mutableListOf<Long>()

    override suspend fun schedule(repeatDays: Set<DayOfWeek>, alarm: Alarm) {
        scheduledAlarms.add(repeatDays to alarm)
    }

    override fun cancel(repeatDays: Set<DayOfWeek>, alarm: Alarm) {
        canceledAlarms.add(repeatDays to alarm)
    }

    override fun cancelByGroup(groupId: Long) {
        canceledGroupIds.add(groupId)
    }

    override fun scheduleTimer(triggerTime: Long) {
        // No-op for testing
    }

    override fun cancelTimer() {
        // No-op for testing
    }
}