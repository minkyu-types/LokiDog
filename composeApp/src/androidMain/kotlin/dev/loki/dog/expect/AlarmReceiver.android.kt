package dev.loki.dog.expect

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.touchlab.kermit.Logger
import dev.loki.alarm.usecase.RescheduleAllAlarmsOnBootUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class AlarmReceiver() : BroadcastReceiver(), KoinComponent {

    private val rescheduleAllAlarmsOnBootUseCase: RescheduleAllAlarmsOnBootUseCase by inject()

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Logger.i { "BOOT_COMPLETED received - rescheduling all alarms" }

                val pendingResult = goAsync()
                val scope = CoroutineScope(Dispatchers.IO)

                scope.launch {
                    try {
                        rescheduleAllAlarmsOnBootUseCase()
                        Logger.i { "All alarms rescheduled successfully after boot" }
                    } catch (e: Exception) {
                        Logger.e(e) { "Failed to reschedule alarms after boot" }
                    } finally {
                        pendingResult.finish()
                    }
                }
            }

            PlatformAlarmScheduler.ACTION_ALARM -> {
                // 타이머 알람인지 확인
                val triggerTime = intent.getLongExtra("trigger_time", -1)
                if (triggerTime != -1L) {
                    Logger.i { "Timer alarm triggered at $triggerTime" }

                    // 타이머 알람 처리
                    val serviceIntent = Intent(context, AlarmService::class.java).apply {
                        putExtra("alarmId", -1L)
                        putExtra("alarmMemo", "타이머")
                        putExtra("trigger_time", triggerTime)
                    }
                    context.startForegroundService(serviceIntent)

                    val activityIntent = Intent(context, dev.loki.dog.AlarmActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra("alarmId", -1L)
                        putExtra("alarmMemo", "타이머")
                    }
                    context.startActivity(activityIntent)
                } else {
                    // 일반 알람이 울리는 경우 - 올바른 키 사용
                    val alarmId = intent.getLongExtra(PlatformAlarmScheduler.KEY_ALARM_ID, -1)
                    val alarmMemo = intent.getStringExtra(PlatformAlarmScheduler.KEY_ALARM_MEMO) ?: "알람"
                    val alarmTime = intent.getLongExtra(PlatformAlarmScheduler.KEY_ALARM_TIME, -1)

                    Logger.i { "Alarm triggered - ID: $alarmId, Memo: $alarmMemo, Time: $alarmTime" }

                    // 알람 서비스 시작 (소리 재생)
                    val serviceIntent = Intent(context, AlarmService::class.java).apply {
                        putExtra(PlatformAlarmScheduler.KEY_ALARM_ID, alarmId)
                        putExtra(PlatformAlarmScheduler.KEY_ALARM_MEMO, alarmMemo)
                    }
                    context.startForegroundService(serviceIntent)

                    // 알람 화면 Activity 시작
                    val activityIntent = Intent(context, dev.loki.dog.AlarmActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        putExtra(PlatformAlarmScheduler.KEY_ALARM_ID, alarmId)
                        putExtra(PlatformAlarmScheduler.KEY_ALARM_MEMO, alarmMemo)
                    }
                    context.startActivity(activityIntent)
                }
            }
        }
    }
}