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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.loki.dog.model.AlarmModel
import dev.loki.dog.theme.ConstraintLight
import dev.loki.dog.theme.OnPrimaryContainerLight
import dev.loki.dog.theme.OnTertiaryLight
import dev.loki.dog.theme.OutlineLight
import dev.loki.dog.theme.PrimaryContainerLight
import dev.loki.dog.theme.PrimaryLight
import dev.loki.dog.theme.Seed
import lokidog.composeapp.generated.resources.Res
import lokidog.composeapp.generated.resources.confirm
import lokidog.composeapp.generated.resources.memo
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeWheelBottomSheet(
    index: Int,
    alarm: AlarmModel,
    onDismiss: () -> Unit,
    onTimeChange: (index: Int, AlarmModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true },
    )

    val hourMinute = alarm.time.split(":").map { it.toInt() }
    var selectedHour by remember { mutableStateOf(hourMinute[0]) }
    var selectedMinute by remember { mutableStateOf(hourMinute[1]) }
    var localMemo by remember { mutableStateOf(alarm.memo) }

    ModalBottomSheet(
        sheetState = sheetState,
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
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                        .background(PrimaryLight, RoundedCornerShape(20.dp))
                        .height(48.dp)
                ) {
                    Text(
                        text = ":",
                        fontSize = 24.sp,
                        color = OnTertiaryLight,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                    )
                }

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
                        items = (0..59).toList(),
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
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text(
                    text = stringResource(Res.string.memo),
                    fontSize = 22.sp,
                    color = OnTertiaryLight,
                    modifier = Modifier
                        .padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = localMemo,
                    onValueChange = {
                        localMemo = it
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 20.sp
                    ),
                    placeholder = {
                        Text(
                            text = "Purpose of this memo",
                            fontSize = 22.sp,
                            color = OutlineLight
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = PrimaryContainerLight,
                        unfocusedContainerColor = OnPrimaryContainerLight,
                        focusedTextColor = OnPrimaryContainerLight,
                        unfocusedTextColor = OnTertiaryLight,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = ConstraintLight
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                )
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
                        }",
                        memo = localMemo
                    )
                    onTimeChange(index, updatedAlarm)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
            ) {
                Text(text = stringResource(Res.string.confirm), fontSize = 18.sp)
            }
        }
    }
}