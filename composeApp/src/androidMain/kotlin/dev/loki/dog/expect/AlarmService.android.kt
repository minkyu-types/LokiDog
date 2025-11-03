package dev.loki.dog.expect

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import co.touchlab.kermit.Logger
import dev.loki.dog.R

actual class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        const val ACTION_STOP_ALARM = "dev.loki.dog.STOP_ALARM"
        private const val NOTIFICATION_ID = 9999
        private const val CHANNEL_ID = "alarm_service_channel"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Logger.d { "AlarmService onCreate" }
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.d { "AlarmService onStartCommand - action: ${intent?.action}" }

        when (intent?.action) {
            ACTION_STOP_ALARM -> {
                stopAlarm()
                return START_NOT_STICKY
            }
            else -> {
                val alarmId = intent?.getLongExtra("alarmId", -1) ?: -1
                val alarmMemo = intent?.getStringExtra("alarmMemo") ?: "알람"

                startForeground(NOTIFICATION_ID, createNotification(alarmMemo))
                acquireWakeLock()
                startAlarmSound()
                startVibration()

                return START_STICKY
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "알람 서비스",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "알람이 울리는 동안 표시되는 알림"
            setSound(null, null) // 서비스 알림은 소리 없음 (MediaPlayer가 소리 재생)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(alarmMemo: String): Notification {
        // 알람 정지 액션
        val stopIntent = Intent(this, AlarmService::class.java).apply {
            action = ACTION_STOP_ALARM
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_app_icon)
            .setContentTitle("알람")
            .setContentText(alarmMemo)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(
                Notification.Action.Builder(
                    null,
                    "정지",
                    stopPendingIntent
                ).build()
            )
            .build()
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
            PowerManager.ACQUIRE_CAUSES_WAKEUP or
            PowerManager.ON_AFTER_RELEASE,
            "LokiDog::AlarmWakeLock"
        ).apply {
            acquire(10 * 60 * 1000L) // 10분 타임아웃
        }
        Logger.d { "WakeLock acquired" }
    }

    private fun startAlarmSound() {
        try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, alarmUri)

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                setAudioAttributes(audioAttributes)
                isLooping = true
                prepare()
                start()
            }

            Logger.d { "Alarm sound started" }
        } catch (e: Exception) {
            Logger.e(e) { "Failed to start alarm sound" }
        }
    }

    private fun startVibration() {
        try {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }

            val pattern = longArrayOf(0, 1000, 1000) // 대기, 진동, 휴식

            val effect = VibrationEffect.createWaveform(pattern, 0) // 0 = 반복
            vibrator?.vibrate(effect)

            Logger.d { "Vibration started" }
        } catch (e: Exception) {
            Logger.e(e) { "Failed to start vibration" }
        }
    }

    private fun stopAlarm() {
        Logger.d { "Stopping alarm" }

        // MediaPlayer 정지
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null

        // 진동 정지
        vibrator?.cancel()
        vibrator = null

        // WakeLock 해제
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        wakeLock = null

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

        Logger.d { "Alarm stopped" }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()
        Logger.d { "AlarmService onDestroy" }
    }
}