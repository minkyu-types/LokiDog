package dev.loki.dog.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.loki.dog.R

/**
 * Android implementation of Lottie animation using Airbnb Lottie library.
 *
 * @param jsonString Resource path (e.g., "files/anim/icon_ai_generate.json")
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
    // Map known resources to R.raw IDs
    val resourceId = when {
        jsonString.contains("icon_ai_generate") -> R.raw.icon_ai_generate
        else -> 0
    }

    val composition by rememberLottieComposition(
        if (resourceId != 0) {
            LottieCompositionSpec.RawRes(resourceId)
        } else {
            // Fallback to JSON string if not a known resource
            LottieCompositionSpec.JsonString(jsonString)
        }
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = if (iterations == Int.MAX_VALUE) LottieConstants.IterateForever else iterations,
        isPlaying = isPlaying
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier
    )
}