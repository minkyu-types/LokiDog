package dev.loki.dog.expect

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.touchlab.kermit.Logger
import dev.loki.dog.AppActivity
import dev.loki.dog.R

actual class AlarmReceiver(): BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("alarmId", -1)
        val alarmMemo = intent.getStringExtra("alarmMemo")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "alarm_channel"

        val channel = NotificationChannel(
            channelId,
            "알람 채널",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "알람 표시용 채널"
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)

        val notification = Notification.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_app_icon)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(alarmMemo)
            .setAutoCancel(false)
            .build()

        notificationManager.notify(alarmId.toInt(), notification)

        val ringIntent = Intent(context, AppActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("alarmId", alarmId)
            putExtra("alarmMemo", alarmMemo)
        }
        context.startActivity(ringIntent)
    }
}