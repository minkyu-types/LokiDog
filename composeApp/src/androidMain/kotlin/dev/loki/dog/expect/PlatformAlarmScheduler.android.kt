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

    companion object {
        const val KEY_ALARM_ID = "alarmId"
        const val KEY_ALARM_TIME = "alarmTime"
        const val KEY_ALARM_MEMO = "alarmMemo"
    }

    override suspend fun schedule(repeatDays: Set<DayOfWeek>, alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
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

    override suspend fun cancel(repeatDays: Set<DayOfWeek>, alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = buildAlarmIntent(alarm.id, alarm.memo, getAlarmTime(repeatDays, alarm.time))
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
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
            action = "dev.loki.dog.ACTION_ALARM" // 반드시 고정
            putExtra(KEY_ALARM_ID, alarmId)
            putExtra(KEY_ALARM_MEMO, memo)
            putExtra(KEY_ALARM_TIME, time)
        }
    }
}