package dev.loki.dog.expect

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.ComponentActivity
import dev.loki.dog.util.getAlarmTime
import kotlinx.datetime.DayOfWeek
import androidx.core.net.toUri
import dev.loki.AlarmScheduler
import dev.loki.alarm.model.Alarm

actual class PlatformAlarmScheduler(
    private val context: Context
): AlarmScheduler {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val ACTION_ALARM = "dev.loki.dog.ACTION_ALARM"
        const val KEY_TRIGGER_TIME = "timerTrigger"
        const val KEY_TIMER_DURATION = "timerDuration"
        const val KEY_ALARM_ID = "alarmId"
        const val KEY_ALARM_TIME = "alarmTime"
        const val KEY_ALARM_MEMO = "alarmMemo"
        private const val TIMER_ALARM_ID = 12345
    }

    override fun scheduleTimer(triggerTime: Long) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM
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

    override fun cancelTimer() {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            TIMER_ALARM_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    override fun schedule(repeatDays: Set<DayOfWeek>, alarm: Alarm) {
        val alarmTime = getAlarmTime(repeatDays, alarm.time)
        val intent = buildAlarmIntent(alarm.id, alarm.memo, getAlarmTime(repeatDays, alarm.time))
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    alarmTime,
                    pendingIntent
                )
            } else {
                requestExactAlarmPermission(context)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                alarmTime,
                pendingIntent
            )
        }
    }

    override fun cancel(repeatDays: Set<DayOfWeek>, alarm: Alarm) {
        val intent = buildAlarmIntent(alarm.id, alarm.memo, getAlarmTime(repeatDays, alarm.time))
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }

    override fun cancelByGroup(groupId: Long) {
        TODO("Not yet implemented")
    }

    private fun requestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = "package:${context.packageName}".toUri()
            }
            if (context !is ComponentActivity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startService(intent)
        }
    }

    private fun buildAlarmIntent(alarmId: Long, memo: String? = null, time: Long? = null): Intent {
        return Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM
            putExtra(KEY_ALARM_ID, alarmId)
            putExtra(KEY_ALARM_MEMO, memo)
            putExtra(KEY_ALARM_TIME, time)
        }
    }
}