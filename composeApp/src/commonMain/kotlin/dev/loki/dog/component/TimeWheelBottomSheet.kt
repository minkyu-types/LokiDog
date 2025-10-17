package dev.loki.dog.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.loki.dog.model.AlarmModel
import dev.loki.dog.theme.ConstraintLight
import dev.loki.dog.theme.OnTertiaryLight
import dev.loki.dog.theme.PrimaryLight
import dev.loki.dog.theme.Seed
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeWheelBottomSheet(
    alarm: AlarmModel,
    onDismiss: () -> Unit,
    onTimeChange: (AlarmModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val hourMinute = alarm.time.split(":").map { it.toInt() }
    var selectedHour by remember { mutableStateOf(hourMinute[0]) }
    var selectedMinute by remember { mutableStateOf(hourMinute[1]) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Seed,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = OnTertiaryLight
            )
        },
        modifier = modifier
            .padding(vertical = 16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                contentAlignment = Alignment.Center
            ) {
//                Box(
//                    contentAlignment = Alignment.Center,
//                    modifier = Modifier
//                        .padding(horizontal = 32.dp)
//                        .fillMaxWidth()
//                        .background(PrimaryLight, RoundedCornerShape(20.dp))
//                        .height(48.dp)
//                ) {
//                    Text(
//                        text = ":",
//                        fontSize = 24.sp,
//                        color = OnTertiaryLight,
//                        modifier = Modifier
//                            .padding(bottom = 4.dp)
//                    )
//                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.fillMaxWidth()
                        .padding(horizontal = 80.dp)
                ) {
                    InfiniteCircularList(
                        width = 120.dp,
                        height = 48.dp,
                        displayedItemSize = 5,
                        items = (0..23).toList(),
                        initialItem = selectedHour,
                        textStyle = TextStyle(
                            fontSize = 18.sp
                        ),
                        textColor = PrimaryLight,
                        selectedTextColor = OnTertiaryLight,
                        onItemSelected = { _, item ->
                            selectedHour = item
                        },
                        modifier = Modifier.weight(1f)
                    )
                    InfiniteCircularList(
                        width = 120.dp,
                        height = 48.dp,
                        displayedItemSize = 5,
                        items = (0..55 step 5).toList(),
                        initialItem = selectedMinute,
                        textStyle = TextStyle(
                            fontSize = 18.sp
                        ),
                        textColor = PrimaryLight,
                        selectedTextColor = Color.White,
                        onItemSelected = { _, item ->
                            selectedMinute = item
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ConstraintLight,
                ),
                onClick = {
                    val updatedAlarm = alarm.copy(
                        time = "${selectedHour.toString().padStart(2, '0')}:${
                            selectedMinute.toString().padStart(2, '0')
                        }"
                    )
                    onTimeChange(updatedAlarm)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text(text = "확인", fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun TimeWheelPicker(
    items: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val visibleItems = 5
    val itemHeight = 48.dp
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val density = LocalDensity.current
    val itemHeightPx = with(density) { itemHeight.toPx() }

    val centerIndex by remember {
        derivedStateOf {
            val offset = lazyListState.firstVisibleItemScrollOffset / itemHeightPx
            (lazyListState.firstVisibleItemIndex + offset + visibleItems / 2)
                .toInt()
                .coerceIn(0, items.lastIndex)
        }
    }

    LaunchedEffect(centerIndex) {
        if (!lazyListState.isScrollInProgress) {
            val targetIndex =
                (lazyListState.firstVisibleItemIndex + visibleItems / 2)
                    .coerceIn(0, items.lastIndex)
            lazyListState.animateScrollToItem(targetIndex)
            onSelected(targetIndex)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
    ) {
        LazyColumn(
            state = lazyListState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .wrapContentWidth()
                .height(240.dp)
                .background(Color.Transparent)
        ) {
            item { Spacer(modifier = Modifier.height(itemHeight)) }

            itemsIndexed(
                items = items,
            ) { index, item ->
                val distance = (centerIndex - index).absoluteValue
                val scale = 1f - (distance * 0.1f).coerceAtMost(0.5f)
                val alpha = 1f - (distance * 0.3f).coerceAtMost(1f)
                val isSelected = index == selectedIndex

                Text(
                    text = item,
                    fontSize = if (isSelected) 24.sp else 20.sp,
                    color = (if (isSelected) Color.White else Color.Gray).copy(alpha = alpha),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(48.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                )
            }

            item { Spacer(modifier = Modifier.height(itemHeight)) }
        }
    }
}

