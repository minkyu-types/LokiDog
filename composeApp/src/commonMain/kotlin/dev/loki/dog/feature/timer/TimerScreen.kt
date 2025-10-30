package dev.loki.dog.feature.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import dev.loki.dog.component.SwipeToDeleteItem
import dev.loki.dog.component.TimerTimeBottomSheet
import dev.loki.dog.theme.ConstraintLight
import dev.loki.dog.theme.OnConstraintLight
import dev.loki.dog.theme.OnPrimaryContainerLight
import dev.loki.dog.theme.OnTertiaryLight
import dev.loki.dog.theme.PrimaryLight
import dev.loki.dog.theme.SurfaceVariantLight
import dev.loki.dog.util.toTimeStr
import dev.loki.dog.util.toTimerTimeStr
import lokidog.composeapp.generated.resources.Res
import lokidog.composeapp.generated.resources.recent_timer_history
import org.jetbrains.compose.resources.stringResource

@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val state by viewModel.state.collectAsState()
    var showTimeBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.setTimerTime(20_000L)
    }

    if (showTimeBottomSheet) {
        val millis = state.totalDuration
        val hours = (millis / 3_600_000).coerceAtMost(23)
        val minutes = (millis / 60_000L) % 60
        val seconds = (millis / 1000L) % 60
        TimerTimeBottomSheet(
            hour = hours.toInt(),
            min = minutes.toInt(),
            sec = seconds.toInt(),
            onDismiss = {
                showTimeBottomSheet = false
            },
            onTimeChange = { hour, min, sec ->
                Logger.d { "qqqq: $hour, $min, $sec" }
                viewModel.setTimerTime(hour * 60 * 60 * 1000L + min * 60 * 1000L + sec * 1000L)
            }
        )
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        if (state.isRunning) {
                            viewModel.pauseTimer()
                        }
                        showTimeBottomSheet = true
                    }
            ) {
                CircularProgressIndicator(
                    progress = {
                        (state.remainingTime.toFloat() / state.totalDuration.toFloat())
                    },
                    color = ConstraintLight,
                    strokeWidth = 12.dp,
                    trackColor = PrimaryLight,
                    strokeCap = StrokeCap.Round,
                    gapSize = (-4).dp,
                    modifier = Modifier
                        .size(240.dp)
                )
                Box(
                    modifier = Modifier
                        .background(PrimaryLight, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    val (hour, min, sec) = state.remainingTime.toTimerTimeStr().split(":")

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = hour,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnTertiaryLight,
                        )
                        Text(
                            text = ":",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnTertiaryLight,
                            modifier = Modifier.padding(bottom = 5.dp, start = 2.dp, end = 2.dp)
                        )
                        Text(
                            text = min,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnTertiaryLight,
                        )
                        Text(
                            text = ":",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnTertiaryLight,
                            modifier = Modifier.padding(bottom = 5.dp, start = 2.dp, end = 2.dp)
                        )
                        Text(
                            text = sec,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnTertiaryLight,
                        )
                    }
                }
            }
            Row {
                FilledIconButton(
                    enabled = state.isRunning,
                    onClick = {
                        viewModel.quitTimer()
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = PrimaryLight
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(88.dp)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = SurfaceVariantLight,
                        modifier = Modifier
                            .size(56.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                FilledIconButton(
                    onClick = {
                        if (state.isRunning) {
                            viewModel.pauseTimer()
                        }
                        if (state.isPaused) {
                            viewModel.resumeTimer()
                        }
                        if (!state.isRunning && !state.isPaused) {
                            viewModel.startTimer()
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = OnConstraintLight
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(88.dp)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = when {
                            state.isRunning -> Icons.Default.Pause
                            else -> Icons.Default.PlayArrow
                        },
                        contentDescription = null,
                        tint = ConstraintLight,
                        modifier = Modifier
                            .size(56.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            TimerHistoryList(
                histories = state.timerHistories,
                onDelete = { history ->
                    viewModel.deleteHistory(history)
                },
                onClick = { history ->
                    viewModel.setTimerTime(history.durationTimeMillis)
                }
            )
        }
    }
}

@Composable
private fun TimerHistoryList(
    histories: List<TimerHistoryModel>,
    onDelete: (TimerHistoryModel) -> Unit,
    onClick: (TimerHistoryModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        Text(
            text = stringResource(Res.string.recent_timer_history),
            color = OnTertiaryLight,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = modifier
        ) {
            itemsIndexed(
                items = histories,
                key = { index, _ -> histories[index].id }
            ) { index, item ->
                SwipeToDeleteItem(
                    item = item,
                    onDelete = {
                        onDelete(item)
                    },
                ) {
                    TimerHistoryItem(
                        history = item,
                        onClick = {
                            onClick(item)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 12.dp)
                    )
                }

                if (index < histories.lastIndex) {
                    HorizontalDivider(
                        color = OnPrimaryContainerLight,
                        thickness = 1.dp,
                    )
                }
            }
        }
    }
}

@Composable
private fun TimerHistoryItem(
    history: TimerHistoryModel,
    onClick: (TimerHistoryModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = history.durationTimeMillis.toTimeStr(),
            fontSize = 32.sp,
            color = OnTertiaryLight,
            modifier = modifier
                .clickable {
                    onClick(history)
                }
        )
    }
}