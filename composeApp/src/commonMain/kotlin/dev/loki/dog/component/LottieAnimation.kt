package dev.loki.dog.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Multiplatform Lottie animation component.
 *
 * Android: Uses Airbnb Lottie library
 * iOS: Uses your custom Lottie implementation
 *
 * @param jsonString The raw JSON string of the Lottie animation
 * @param modifier Modifier for the animation container
 * @param iterations Number of times to loop the animation (Int.MAX_VALUE for infinite loop)
 * @param isPlaying Whether the animation should be playing
 */
@Composable
expect fun LottieAnimation(
    jsonString: String,
    modifier: Modifier = Modifier,
    iterations: Int = Int.MAX_VALUE,
    isPlaying: Boolean = true
)