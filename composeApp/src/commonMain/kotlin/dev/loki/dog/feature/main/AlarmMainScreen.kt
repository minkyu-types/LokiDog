package dev.loki.dog.feature.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.loki.dog.component.SelectionModeItem
import dev.loki.dog.component.SwipeToDeleteItem
import dev.loki.dog.model.AlarmGroupModel

@Composable
fun AlarmMainScreen(
    isSelectionMode: Boolean,
    viewModel: AlarmMainViewModel,
    onAlarmGroupClick: (AlarmGroupModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val selectedItems: MutableSet<AlarmGroupModel> = remember { mutableStateSetOf() }

    if (state.value.alarmGroupList.isEmpty()) {
        Text(
            text = "알람을 추가해보세요",
            fontSize = 20.sp,
        )
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
        ) {
            items(
                items = state.value.alarmGroupList,
                key = { it.id }
            ) { item ->
                if (isSelectionMode) {
                    AlarmGroupSelectionModeItem(
                        isChecked = (item in selectedItems),
                        alarmGroup = item,
                        onCheckedChange = { group, isChecked ->
                            if (isChecked) {
                                selectedItems.add(group)
                            } else {
                                selectedItems.remove(group)
                            }
                        }
                    )
                } else {
                    AlarmGroupSwipeToDeleteItem(
                        alarmGroup = item,
                        onAlarmGroupClick = onAlarmGroupClick,
                        onDelete = { group ->
                            viewModel.deleteAlarmGroup(group)
                        }
                    )
                }
            }
        }
    }

}

@Composable
private fun AlarmGroupSelectionModeItem(
    isChecked: Boolean,
    alarmGroup: AlarmGroupModel,
    onCheckedChange: (AlarmGroupModel, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SelectionModeItem(
        item = alarmGroup,
        isChecked = isChecked,
        onCheckedChange = { group, isChecked ->
            onCheckedChange(group, isChecked)
        },
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
            ) {
                Text(
                    text = alarmGroup.title,
                    fontSize = 20.sp,
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowCircleRight,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }
    )
}

@Composable
private fun AlarmGroupSwipeToDeleteItem(
    alarmGroup: AlarmGroupModel,
    onAlarmGroupClick: (AlarmGroupModel) -> Unit,
    onDelete: (AlarmGroupModel) -> Unit,
    modifier: Modifier = Modifier
) {
    SwipeToDeleteItem(
        item = alarmGroup,
        onDelete = onDelete,
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .clickable {
                        onAlarmGroupClick(alarmGroup)
                    }
            ) {
                Text(
                    text = alarmGroup.title,
                    fontSize = 20.sp,
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.ArrowCircleRight,
                    contentDescription = null,
                    tint = Color.Black
                )
            }
        }
    )
}