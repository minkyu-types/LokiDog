package dev.loki.dog.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.loki.dog.model.AlarmModel
import dev.loki.dog.theme.ConstraintLight
import dev.loki.dog.theme.PrimaryLight
import dev.loki.dog.theme.Seed

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
        modifier = modifier
            .padding(vertical = 16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier.fillMaxWidth()
                    .padding(horizontal = 40.dp)
            ) {
                TimeWheelPicker(
                    items = (0..23).map { it.toString().padStart(2, '0') },
                    selectedIndex = selectedHour,
                    onSelected = { selectedHour = it },
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = ":",
                    fontSize = 32.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .height(160.dp)
                )
                TimeWheelPicker(
                    items = (0..59).map { it.toString().padStart(2, '0') },
                    selectedIndex = selectedMinute,
                    onSelected = { selectedMinute = it },
                    modifier = Modifier.weight(1f)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.Center)
                    .padding(horizontal = 32.dp)
                    .background(
                        PrimaryLight.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    )
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
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
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text("확인")
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
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = (selectedIndex + 2)
    )

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
                .height(168.dp)
                .background(Color.Transparent)
                .padding(vertical = 8.dp)
        ) {
            itemsIndexed(
                items = items,
            ) { index, item ->
                val isSelected = index == selectedIndex

                Text(
                    text = item,
                    fontSize = if (isSelected) 24.sp else 20.sp,
                    color = if (isSelected) Color.White else Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onSelected(index) }
                )
            }
        }
    }
}

