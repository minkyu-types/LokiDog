package dev.loki.dog.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.airbnb.lottie.RenderMode
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
            // Force software rendering to ensure gradients and effects render correctly
            .graphicsLayer {
                // Disable hardware acceleration for better color rendering
                alpha = 0.99f
            },
        // Enable merge paths for better compatibility
        enableMergePaths = true,
        // Use SOFTWARE render mode to ensure gradients and blur effects display correctly
        // HARDWARE mode may cause color/gradient issues on some devices
        renderMode = RenderMode.SOFTWARE
    )
}