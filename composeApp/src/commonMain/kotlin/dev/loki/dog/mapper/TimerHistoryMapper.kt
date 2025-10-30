package dev.loki.dog.mapper

import dev.loki.dog.feature.timer.TimerHistoryModel
import dev.loki.timerhistory.model.TimerHistory

class TimerHistoryMapper {

    fun mapToDomain(timerHistory: TimerHistoryModel): TimerHistory {
        return TimerHistory(
            id = timerHistory.id,
            durationTimeMillis = timerHistory.durationTimeMillis,
        )
    }

    fun mapToPresentation(timerHistory: TimerHistory): TimerHistoryModel {
        return TimerHistoryModel(
            id = timerHistory.id,
            durationTimeMillis = timerHistory.durationTimeMillis,
        )
    }
}