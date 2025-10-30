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
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
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
        } else {
            // 알람이 울리는 경우
            val alarmId = intent.getLongExtra("alarmId", -1)
            val alarmMemo = intent.getStringExtra("alarmMemo") ?: "알람"

            Logger.i { "Alarm triggered - ID: $alarmId, Memo: $alarmMemo" }

            // 알람 서비스 시작 (소리 재생)
            val serviceIntent = Intent(context, AlarmService::class.java).apply {
                putExtra("alarmId", alarmId)
                putExtra("alarmMemo", alarmMemo)
            }
            context.startForegroundService(serviceIntent)

            // 알람 화면 Activity 시작
            val activityIntent = Intent(context, dev.loki.dog.AlarmActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("alarmId", alarmId)
                putExtra("alarmMemo", alarmMemo)
            }
            context.startActivity(activityIntent)
        }
    }
}