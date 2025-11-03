package dev.loki.dog.expect

import androidx.compose.runtime.Composable

/**
 * 플랫폼별 알람 다이얼로그 표시
 * Android: 별도 Activity 사용하므로 빈 Composable
 * iOS: 전체화면 Dialog 표시
 */
@Composable
expect fun AlarmDialogContainer()