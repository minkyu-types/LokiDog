package dev.loki.dog.feature.temp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowRight
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.loki.dog.component.SelectionModeItem
import dev.loki.dog.component.SwipeToDeleteItem
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.theme.OnPrimaryContainerLight
import dev.loki.dog.theme.OnTertiaryLight
import dev.loki.dog.theme.SurfaceVariantLight

@Composable
fun TempAlarmGroupsScreen(
    viewModel: TempAlarmGroupsViewModel,
    onBack: () -> Unit,
    onAlarmGroupClick: (AlarmGroupModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isSelectionMode by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val selectedItems: MutableSet<AlarmGroupModel> = remember { mutableStateSetOf() }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->

        }
    }

    LaunchedEffect(isSelectionMode) {
        selectedItems.clear()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {
        item {
            TempAlarmGroupTopBar(
                isSelectionMode = isSelectionMode,
                onBack = onBack,
                onSelectionModeClick = {
                    isSelectionMode = !isSelectionMode
                }
            )
        }

        itemsIndexed(
            items = state.tempAlarmGroupList,
            key = { _, alarm -> alarm.id }
        ) { index, item ->
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
                    onAlarmGroupClick = { group ->
                        onAlarmGroupClick(group)
                    },
                    onDelete = { group ->
                        viewModel.deleteTempAlarmGroup(group)
                    }
                )
            }

            if (index < state.tempAlarmGroupList.lastIndex) {
                HorizontalDivider(
                    color = OnPrimaryContainerLight,
                    thickness = 1.dp,
                )
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
        onCheckedChange = { group, checked ->
            onCheckedChange(group, checked)
        },
        content = {
            TempAlarmGroupItem(
                alarmGroup = alarmGroup,
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onCheckedChange(alarmGroup, !isChecked)
            }
            .padding(start = 24.dp, end = 16.dp)
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
            TempAlarmGroupItem(
                alarmGroup = alarmGroup,
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onAlarmGroupClick(alarmGroup)
            }
            .padding(start = 24.dp, end = 16.dp)
    )
}

@Composable
private fun TempAlarmGroupItem(
    alarmGroup: AlarmGroupModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 40.dp)
        ) {
            Text(
                text = alarmGroup.title,
                fontSize = 20.sp,
                color = OnTertiaryLight
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Sharp.ArrowRight,
            contentDescription = null,
            tint = OnTertiaryLight,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        )
    }
}


@Composable
private fun TempAlarmGroupTopBar(
    isSelectionMode: Boolean,
    onBack: () -> Unit,
    onSelectionModeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clipToBounds()
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            tint = OnTertiaryLight,
            contentDescription = null,
            modifier = Modifier
                .clickable {
                    onBack()
                }
        )

        Text(
            text = "Saved", // TODO: stringResource로 변경
            fontSize = 24.sp,
            color = OnTertiaryLight,
            modifier = modifier
                .padding(start = 12.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = if (isSelectionMode) Icons.Default.EditOff else Icons.Default.Edit,
            contentDescription = null,
            tint = SurfaceVariantLight,
            modifier = Modifier
                .clickable {
                    onSelectionModeClick()
                }
        )
    }
}