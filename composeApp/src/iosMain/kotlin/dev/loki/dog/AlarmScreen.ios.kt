package dev.loki.dog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.loki.dog.expect.AlarmService

/**
 * iOS용 전체화면 알람 화면
 */
@Composable
fun AlarmScreenDialog(
    alarmMemo: String?,
    onDismiss: () -> Unit
) {
    DisposableEffect(Unit) {
        // 알람 소리 시작
        AlarmService.playAlarmSound()

        onDispose {
            // 화면 닫을 때 소리 정지
            AlarmService.stopAlarmSound()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = "알람",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = alarmMemo ?: "알람",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Button(
                    onClick = {
                        AlarmService.stopAlarmSound()
                        onDismiss()
                    },
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = "정지",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

/**
 * 전역 상태로 알람 다이얼로그 표시 여부 관리
 */
object AlarmDialogState {
    private var _isShowing = mutableStateOf(false)
    private var _alarmMemo = mutableStateOf<String?>(null)

    val isShowing: Boolean get() = _isShowing.value
    val alarmMemo: String? get() = _alarmMemo.value

    fun show(memo: String?) {
        _alarmMemo.value = memo
        _isShowing.value = true
        println("🔔 iOS: AlarmDialogState.show() - memo: $memo")
    }

    fun hide() {
        _isShowing.value = false
        _alarmMemo.value = null
        println("🔕 iOS: AlarmDialogState.hide()")
    }
}