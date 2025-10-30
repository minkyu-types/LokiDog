package dev.loki.alarm_data.mapper

import dev.loki.alarm_data.model.TimerHistoryEntity
import dev.loki.timerhistory.model.TimerHistory

class TimerHistoryMapper {

    fun mapToDomain(timerHistory: TimerHistoryEntity): TimerHistory {
        return TimerHistory(
            id = timerHistory.id,
            durationTimeMillis = timerHistory.durationTimeMillis,
        )
    }

    fun mapToData(timerHistory: TimerHistory): TimerHistoryEntity {
        return TimerHistoryEntity(
            id = timerHistory.id,
            durationTimeMillis = timerHistory.durationTimeMillis,
        )
    }
}