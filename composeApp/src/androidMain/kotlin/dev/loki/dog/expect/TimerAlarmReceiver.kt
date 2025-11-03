package dev.loki.dog.expect

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.touchlab.kermit.Logger

class TimerAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Logger.i { "Timer alarm triggered" }

        val timerMemo = intent.getStringExtra("timerMemo") ?: "타이머 종료"

        // 알람 서비스 시작 (소리 재생)
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("alarmId", -1L) // 타이머는 -1로 구분
            putExtra("alarmMemo", timerMemo)
        }
        context.startForegroundService(serviceIntent)

        // 알람 화면 Activity 시작
        val activityIntent = Intent(context, dev.loki.dog.AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("alarmId", -1L)
            putExtra("alarmMemo", timerMemo)
        }
        context.startActivity(activityIntent)
    }
}