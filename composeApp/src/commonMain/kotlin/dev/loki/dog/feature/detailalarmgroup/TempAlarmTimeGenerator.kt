package dev.loki.dog.feature.detailalarmgroup

object TempAlarmTimeGenerator {
    private var currHour = 0
    private var currMinute = 0

    fun nextTime(): String {
        currMinute += 5

        if (currMinute >= 60) {
            currMinute = 0
            currHour++
        }

        if (currHour >= 24) {
            currHour = 0
        }

        return "${currHour.toString().padStart(2, '0')}:${currMinute.toString().padStart(2, '0')}"
    }

    fun reset() {
        currHour = 0
        currMinute = 0
    }
}