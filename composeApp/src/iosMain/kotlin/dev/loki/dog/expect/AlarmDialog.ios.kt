package dev.loki.dog.expect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.loki.dog.AlarmDialogState
import dev.loki.dog.AlarmScreenDialog

/**
 * iOS용 알람 다이얼로그 컨테이너
 * 전역 상태를 관찰하여 알람 화면 표시
 */
@Composable
actual fun AlarmDialogContainer() {
    var showDialog by remember { mutableStateOf(false) }
    var alarmMemo by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(AlarmDialogState.isShowing) {
        if (AlarmDialogState.isShowing) {
            showDialog = true
            alarmMemo = AlarmDialogState.alarmMemo
        } else {
            showDialog = false
        }
    }

    if (showDialog) {
        AlarmScreenDialog(
            alarmMemo = alarmMemo,
            onDismiss = {
                showDialog = false
                AlarmDialogState.hide()
            }
        )
    }
}