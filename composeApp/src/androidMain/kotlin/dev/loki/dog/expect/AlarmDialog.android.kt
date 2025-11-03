package dev.loki.dog.expect

import androidx.compose.runtime.Composable

/**
 * Android는 별도 AlarmActivity를 사용하므로 빈 Composable
 */
@Composable
actual fun AlarmDialogContainer() {
    // Android는 AlarmActivity를 사용하므로 아무것도 하지 않음
}