package dev.loki.dog.expect

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

actual class AlarmService(
) : Service() {

    private val context: Context = applicationContext
    private var _alarmManager: AlarmManager? = null
    val alarmManager: AlarmManager = _alarmManager!!

    init {
        _alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    }

    fun getIntent(requestId: Int, intent: Intent) {
        val pendingIntent = PendingIntent.getService(context, requestId, intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        if (pendingIntent != null && _alarmManager != null) {
            alarmManager.cancel(pendingIntent)
        }
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(
                0L,
                pendingIntent
            ),
            pendingIntent
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}