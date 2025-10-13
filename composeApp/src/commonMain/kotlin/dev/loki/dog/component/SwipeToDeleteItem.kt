package dev.loki.dog.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun <T> SwipeToDeleteItem(
    item: T,
    content: @Composable () -> Unit,
    onDelete: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val actionWidthDp = 80.dp
    val actionWidthPx = with(density) { actionWidthDp.toPx() }

    var offsetX by remember { mutableStateOf(0f) }
    val swipeThreshold = actionWidthPx / 2
    val maxSwipe = -actionWidthPx

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
        ) {
            if (offsetX < 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(actionWidthDp)
                        .align(Alignment.CenterEnd)
                        .clipToBounds()
                        .clickable {
                            onDelete(item)
                        }
                        .background(Color.Red),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .background(Color.Transparent)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            offsetX = if (offsetX < -swipeThreshold) {
                                maxSwipe
                            } else {
                                0f
                            }
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        val newOffset = offsetX + dragAmount
                        offsetX = newOffset.coerceIn(maxSwipe, 0f)
                    }
                }
                .padding(end = 8.dp)
        ) {
            content()
        }
    }
}