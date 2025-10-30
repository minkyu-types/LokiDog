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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.loki.dog.theme.ConstraintLight
import dev.loki.dog.theme.OnTertiaryLight
import dev.loki.dog.theme.PrimaryLight
import dev.loki.dog.theme.Seed
import lokidog.composeapp.generated.resources.Res
import lokidog.composeapp.generated.resources.confirm
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerTimeBottomSheet(
    hour: Int,
    min: Int,
    sec: Int,
    onDismiss: () -> Unit,
    onTimeChange: (hour: Int, min: Int, sec: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true },
    )
    var selectedHour by remember { mutableStateOf(hour) }
    var selectedMinute by remember { mutableStateOf(min) }
    var selectedSecond by remember { mutableStateOf(sec) }

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
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                        .background(PrimaryLight, RoundedCornerShape(20.dp))
                        .height(48.dp)
                )

                Row(
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
                    Text(text = "시간", color = OnTertiaryLight, modifier = Modifier.padding(bottom = 3.dp))
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
                        selectedTextColor = OnTertiaryLight,
                        onItemSelected = { _, item ->
                            selectedMinute = item
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = "분", color = OnTertiaryLight, modifier = Modifier.padding(bottom = 3.dp))
                    InfiniteCircularList(
                        width = 120.dp,
                        height = 48.dp,
                        displayedItemSize = 5,
                        items = (0..59).toList(),
                        initialItem = selectedSecond,
                        textStyle = TextStyle(
                            fontSize = 18.sp
                        ),
                        textColor = PrimaryLight,
                        selectedTextColor = OnTertiaryLight,
                        onItemSelected = { _, item ->
                            selectedSecond = item
                        },
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = "초", color = OnTertiaryLight,  modifier = Modifier.padding(bottom = 3.dp))
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ConstraintLight,
                ),
                onClick = {
                    onTimeChange(selectedHour,selectedMinute,selectedSecond)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 32.dp)
            ) {
                Text(text = stringResource(Res.string.confirm), fontSize = 18.sp)
            }
        }
    }
}