package dev.loki.dog.component

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Pre-configured animated icon component using Lottie animation.
 *
 * @param modifier Modifier for the icon
 * @param size Size of the icon (default 48.dp)
 * @param isPlaying Whether the animation should be playing
 */
@Composable
fun AnimatedAiIcon(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    isPlaying: Boolean = true
) {
    LottieAnimation(
        jsonString = "files/anim/icon_ai_generate.json",
        modifier = modifier.size(size),
        iterations = Int.MAX_VALUE,
        isPlaying = isPlaying
    )
}

/**
 * Generic animated icon from resource path.
 *
 * @param resourcePath Resource identifier (e.g., "icon_ai_generate" or "files/anim/my_animation.json")
 * @param modifier Modifier for the icon
 * @param size Size of the icon
 * @param iterations Number of times to loop (Int.MAX_VALUE for infinite)
 * @param isPlaying Whether the animation should be playing
 */
@Composable
fun AnimatedIconFromResource(
    resourcePath: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    iterations: Int = Int.MAX_VALUE,
    isPlaying: Boolean = true
) {
    LottieAnimation(
        jsonString = resourcePath,
        modifier = modifier.size(size),
        iterations = iterations,
        isPlaying = isPlaying
    )
}