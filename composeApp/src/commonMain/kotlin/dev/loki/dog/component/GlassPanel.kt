package dev.loki.dog.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    corner: Dp = 20.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    backgroundColor: Color = Color.White.copy(alpha = 0.12f),
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(corner)

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor, shape)
            .border(1.dp, Color.White.copy(alpha = 0.25f), shape)
    ) {
        // 5) 상단 하이라이트 그라디언트(빛 반사)
        Box(
            Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = 0.22f),
                        0.20f to Color.White.copy(alpha = 0.10f),
                        1.0f to Color.Transparent
                    )
                )
        )
        // 6) 내용
        Column(Modifier.padding(contentPadding)) {
            content()
        }
    }
}
