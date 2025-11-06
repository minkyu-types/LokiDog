package dev.loki.dog.component

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * iOS implementation of Lottie animation.
 * TODO: Implement with your preferred iOS Lottie library
 *
 * @param jsonString The raw JSON string of the Lottie animation
 * @param modifier Modifier for the animation container
 * @param iterations Number of times to loop the animation (Int.MAX_VALUE for infinite loop)
 * @param isPlaying Whether the animation should be playing
 */
@Composable
actual fun LottieAnimation(
    jsonString: String,
    modifier: Modifier,
    iterations: Int,
    isPlaying: Boolean
) {
    // TODO: iOS에서 사용할 Lottie 라이브러리로 교체해주세요
    // 예: SwiftUI의 Lottie 또는 다른 KMP 호환 라이브러리
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text("Lottie Animation (iOS - Not Implemented)")
    }
}