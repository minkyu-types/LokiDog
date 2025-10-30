package dev.loki.dog

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dev.loki.dog.expect.AlarmReceiver
import dev.loki.dog.feature.timer.TimerState

class TimerManager(private val context: Context) {
    private var timerState = TimerState(
        totalDuration = 0L,
        remainingTime = 0L
    )
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun startTimer(durationMillis: Long) {
        timerState = TimerState(
            totalDuration = durationMillis,
            remainingTime = durationMillis,
            isRunning = true,
            alarmTime = System.currentTimeMillis() + durationMillis
        )

        scheduleAlarm(timerState.alarmTime)
    }

    fun pauseTimer() {
        if(!timerState.isRunning || timerState.isPaused) return

        timerState.apply {
            isPaused = true
            isRunning = false
            pausedAt = System.currentTimeMillis()
            remainingTime = alarmTime - pausedAt
            if (remainingTime < 0) remainingTime = 0
        }

        cancelAlarm()
    }

    fun resumeTimer() {
        if (!timerState.isPaused) return

        timerState.apply {
            isPaused = false
            isRunning = true
            alarmTime = System.currentTimeMillis() + remainingTime
        }

        scheduleAlarm(timerState.alarmTime)
    }

    private fun scheduleAlarm(triggerTime: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "TIMER_ALARM"
            putExtra("trigger_time", triggerTime)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            TIMER_ALARM_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    private fun cancelAlarm() {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            TIMER_ALARM_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    companion object {
        private const val TIMER_ALARM_ID = 12345
    }
}