package dev.loki.dog.feature.addalarmgroup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.loki.dog.component.SelectionModeItem
import dev.loki.dog.component.SwipeToDeleteItem
import dev.loki.dog.component.TimeWheelBottomSheet
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.model.AlarmModel
import dev.loki.dog.theme.ConstraintLight
import dev.loki.dog.theme.InverseOnSurfaceLight
import dev.loki.dog.theme.OnPrimaryContainerLight
import dev.loki.dog.theme.OnTertiaryLight
import dev.loki.dog.theme.OutlineLight
import dev.loki.dog.theme.OutlineVariantLight
import dev.loki.dog.theme.PrimaryContainerLight
import kotlinx.datetime.DayOfWeek
import lokidog.composeapp.generated.resources.Res
import lokidog.composeapp.generated.resources.alarms
import lokidog.composeapp.generated.resources.alarms_max_description
import lokidog.composeapp.generated.resources.save
import org.jetbrains.compose.resources.stringResource
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun AddAlarmGroupScreen(
    groupId: Long,
    onSaveOrSaveTemp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = getKoin().get<AddAlarmGroupViewModel>()
    var isSelectionMode by remember { mutableStateOf(false) }
    val selectedAlarmIds = remember { mutableStateListOf<Long>() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    var alarmGroup by remember { mutableStateOf(AlarmGroupModel.createTemp()) }
    val focusManager = LocalFocusManager.current

    var showTimeBottomSheet by remember { mutableStateOf(false) }
    var timeBottomSheetData by remember { mutableStateOf<AlarmModel?>(null) }

    if (showTimeBottomSheet) {
        TimeWheelBottomSheet(
            index = alarmGroup.alarms.indexOf(timeBottomSheetData),
            alarm = timeBottomSheetData!!,
            onDismiss = {
                showTimeBottomSheet = false
            },
            onTimeChange = { index, updatedAlarm ->
                val prevAlarms = alarmGroup.alarms
                val targetAlarm = prevAlarms[index]
                val newAlarms = prevAlarms.toMutableList() - targetAlarm + updatedAlarm
                alarmGroup = alarmGroup.copy(
                    alarms = newAlarms
                )
            }
        )
    }

    LaunchedEffect(groupId) {
        if (groupId != 0L) {
            viewModel.getAlarmGroup(groupId)
        } else {
            viewModel.getTempAlarmGroup()
        }
    }

    LaunchedEffect(state.tempAlarmGroup) {
        alarmGroup = state.tempAlarmGroup
    }

    Box(
        modifier = Modifier
    ) {
        Column {
            AddAlarmGroupTopBar(
                isSelectionMode = isSelectionMode,
                alarmGroup = alarmGroup,
                onSaveOrSaveTemp = onSaveOrSaveTemp,
                onSelectionModeChange = {
                    isSelectionMode = it
                    selectedAlarmIds.clear()
                },
                onAddNewAlarm = { newAlarm ->
                    alarmGroup = alarmGroup.copy(
                        alarms = alarmGroup.alarms + newAlarm
                    )
                },
                onSaveTempAlarmGroup = { alarmGroup ->
                    viewModel.saveTempAlarmGroup(alarmGroup)
                    onSaveOrSaveTemp()
                },
            )

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus()
                    }
                    .padding(bottom = 84.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    EditableAlarmGroupHeader(
                        alarmGroup = alarmGroup,
                        onItemEdit = { editedAlarmGroup ->
                            alarmGroup = editedAlarmGroup
                        },
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(Res.string.alarms, alarmGroup.alarms.size), // TODO(유료 구독 상태에 따라 최대 개수 조절)
                        color = OnTertiaryLight,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(start = 32.dp)
                    )
                    Text(
                        text = stringResource(Res.string.alarms_max_description, 10),
                        color = OutlineVariantLight,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(start = 32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                itemsIndexed(
                    items = alarmGroup.alarms.sortedBy {
                        val time = it.time.split(":")
                        time[0].toInt() * 60 + time[1].toInt()
                    },
                    key = { _, alarm -> "${alarmGroup.alarms.indexOf(alarm)}/${alarm.id}/${alarm.time}" }
                ) { index, alarm ->
                    if (isSelectionMode) {
                        SelectableEditableAlarmItem(
                            isChecked = (alarm.id in selectedAlarmIds),
                            alarm = alarm,
                            onAlarmEdit = { editedAlarm ->
                                val updatedAlarms = alarmGroup.alarms.map { alarm ->
                                    if (alarm.id == editedAlarm.id) editedAlarm else alarm
                                }
                                alarmGroup = alarmGroup.copy(
                                    alarms = updatedAlarms
                                )
                            },
                            onCheckedChange = { selectedAlarmId ->
                                if (selectedAlarmId in selectedAlarmIds) {
                                    selectedAlarmIds.remove(selectedAlarmId)
                                } else {
                                    selectedAlarmIds.add(selectedAlarmId)
                                }
                            }
                        )
                    } else {
                        EditableAlarmItem(
                            alarm = alarm,
                            onAlarmEdit = { editedAlarm ->
                                val updatedAlarms = alarmGroup.alarms.map { alarm ->
                                    if (alarm.id == editedAlarm.id) editedAlarm else alarm
                                }
                                alarmGroup = alarmGroup.copy(
                                    alarms = updatedAlarms
                                )
                                viewModel.updateAlarm(editedAlarm)
                            },
                            onAlarmClick = { alarm ->
                                showTimeBottomSheet = true
                                timeBottomSheetData = alarm
                            },
                            onAlarmDelete = { deleteAlarm ->
                                alarmGroup = alarmGroup.copy(
                                    alarms = alarmGroup.alarms - deleteAlarm
                                )
                                viewModel.deleteAlarm(deleteAlarm)
                            },
                        )
                    }

                    if (index < alarmGroup.alarms.lastIndex) {
                        HorizontalDivider(
                            color = OnPrimaryContainerLight,
                            thickness = 1.dp,
                            modifier = Modifier
                                .padding(horizontal = 32.dp)
                        )
                    }
                }

                item {
                    Button(
                        enabled = (alarmGroup.title.isNotEmpty() && alarmGroup.description.isNotEmpty()
                                && alarmGroup.alarms.isNotEmpty() && alarmGroup.repeatDays.isNotEmpty()),
                        onClick = {
                            viewModel.saveAlarmGroup(alarmGroup)
                            onSaveOrSaveTemp()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ConstraintLight,
                            contentColor = OnTertiaryLight
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = stringResource(Res.string.save), fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AddAlarmGroupTopBar(
    alarmGroup: AlarmGroupModel,
    isSelectionMode: Boolean,
    onSaveOrSaveTemp: () -> Unit,
    onSelectionModeChange: (Boolean) -> Unit,
    onAddNewAlarm: (AlarmModel) -> Unit,
    onSaveTempAlarmGroup: (AlarmGroupModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 20.dp)
    ) {
        IconButton(
            onClick = {
                onSaveOrSaveTemp()
            },
            modifier = Modifier
                .size(IconButtonDefaults.extraSmallContainerSize())
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBackIosNew,
                tint = OnTertiaryLight,
                contentDescription = null,
            )
        }
        Spacer(modifier = Modifier.weight(1f))


        IconButton(
            onClick = {
                onSelectionModeChange(!isSelectionMode)
            },
            modifier = Modifier
                .size(IconButtonDefaults.extraSmallContainerSize())
        ) {
            Icon(
                imageVector = if (isSelectionMode) Icons.Default.EditOff else Icons.Default.Edit,
                contentDescription = null,
                tint = OnTertiaryLight
            )
        }
        Spacer(modifier = Modifier.width(24.dp))

        if (alarmGroup.id == 0L || alarmGroup.isTemp) {
            IconButton(
                enabled = alarmGroup.title.isNotBlank() && alarmGroup.description.isNotBlank(),
                onClick = {
                    onSaveTempAlarmGroup(alarmGroup)
                },
                modifier = Modifier
                    .size(IconButtonDefaults.extraSmallContainerSize())
            ) {
                Icon(
                    imageVector = Icons.Default.LibraryAdd,
                    contentDescription = null,
                    tint = OnTertiaryLight
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
        }

        IconButton(
            enabled = alarmGroup.title.isNotBlank() && alarmGroup.description.isNotBlank() && alarmGroup.alarms.size < 10,
            onClick = {
                val newAlarm = AlarmModel.createTemp(alarmGroup.id, alarmGroup.alarms)
                onAddNewAlarm(newAlarm)
            },
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = OnTertiaryLight,
                disabledContentColor = OutlineLight
            ),
            modifier = Modifier
                .size(IconButtonDefaults.extraSmallContainerSize())
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun EditableAlarmGroupHeader(
    alarmGroup: AlarmGroupModel,
    onItemEdit: (AlarmGroupModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var localRepeatDayOfWeeks by remember { mutableStateOf(alarmGroup.repeatDays) }
    var localTitle by remember { mutableStateOf(alarmGroup.title) }
    var localDescription by remember { mutableStateOf(alarmGroup.description) }
    val scrollState = rememberScrollState()

    LaunchedEffect(alarmGroup) {
        localTitle = alarmGroup.title
        localDescription = alarmGroup.description
        localRepeatDayOfWeeks = alarmGroup.repeatDays
    }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 8.dp)
        ) {
            TextField(
                value = localTitle,
                onValueChange = {
                    localTitle = it

                    val detectedDay = detectDayOfWeekFromText(it)
                    val updatedRepeatDays = if (detectedDay != null) {
                        setOf(detectedDay)
                    } else {
                        alarmGroup.repeatDays
                    }
                    localRepeatDayOfWeeks = updatedRepeatDays

                    val newData = alarmGroup.copy(
                        title = localTitle,
                        repeatDays = updatedRepeatDays,
                    )

                    onItemEdit(newData)
                },
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 28.sp
                ),
                placeholder = {
                    Text(
                        text = "Title",
                        fontSize = 28.sp,
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
                    .weight(1f)
                    .padding(end = 8.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = alarmGroup.isActivated,
                onCheckedChange = {
                    onItemEdit(
                        alarmGroup.copy(
                            isActivated = it
                        )
                    )
                },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = ConstraintLight,
                    uncheckedTrackColor = InverseOnSurfaceLight
                ),
                modifier = Modifier
                    .padding(end = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        DayOfWeekLazyRow(
            selectedDayOfWeeks = localRepeatDayOfWeeks,
            onClick = { dayOfWeek ->
                if (dayOfWeek in localRepeatDayOfWeeks) {
                    localRepeatDayOfWeeks -= dayOfWeek
                } else {
                    localRepeatDayOfWeeks += dayOfWeek
                }
                onItemEdit(
                    alarmGroup.copy(
                        repeatDays = localRepeatDayOfWeeks
                    )
                )
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = localDescription,
            onValueChange = {
                localDescription = it
                val newData = alarmGroup.copy(
                    description = localDescription,
                )
                onItemEdit(newData)
            },
            textStyle = TextStyle(
                fontSize = 18.sp
            ),
            maxLines = 5,
            placeholder = {
                Text(
                    text = "Description",
                    fontSize = 18.sp,
                    color = OutlineLight
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = PrimaryContainerLight,
                unfocusedContainerColor = OnPrimaryContainerLight,
                focusedTextColor = OnPrimaryContainerLight,
                unfocusedTextColor = OnTertiaryLight,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = ConstraintLight
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp, max = 120.dp + (18.sp.value * 5).dp)
                .padding(horizontal = 8.dp)
                .verticalScroll(scrollState)
        )
    }
}

@Composable
private fun DayOfWeekLazyRow(
    selectedDayOfWeeks: Set<DayOfWeek> = emptySet(),
    onClick: (DayOfWeek) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        val dayOfWeeks = DayOfWeek.entries
        dayOfWeeks.forEach { dayOfWeek ->
            val dayOfWeekStr = dayOfWeek.name.take(3)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (dayOfWeek in selectedDayOfWeeks) {
                            OnPrimaryContainerLight
                        } else {
                            Color.Transparent
                        }
                    )
                    .clickable {
                        onClick(dayOfWeek)

                    }
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = dayOfWeekStr,
                    fontSize = 14.sp,
                    color = if (dayOfWeek in selectedDayOfWeeks) ConstraintLight else OnTertiaryLight,
                )
            }
        }
    }
}

@Composable
private fun SelectableEditableAlarmItem(
    isChecked: Boolean,
    alarm: AlarmModel,
    onAlarmEdit: (AlarmModel) -> Unit,
    onCheckedChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    SelectionModeItem(
        item = alarm,
        isChecked = isChecked,
        onCheckedChange = { selectedAlarm, _ ->
            onCheckedChange(selectedAlarm.id)
        },
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onCheckedChange(alarm.id)
            }
            .padding(start = 32.dp, end = 24.dp)
    ) {
        AlarmItem(
            alarm = alarm,
            onAlarmEdit = onAlarmEdit,
            modifier = modifier
        )
    }
}

@Composable
private fun EditableAlarmItem(
    alarm: AlarmModel,
    onAlarmClick: (AlarmModel) -> Unit,
    onAlarmEdit: (AlarmModel) -> Unit,
    onAlarmDelete: (AlarmModel) -> Unit,
    modifier: Modifier = Modifier
) {
    SwipeToDeleteItem(
        item = alarm,
        onDelete = {
            onAlarmDelete(alarm)
        },
        content = {
            AlarmItem(
                alarm = alarm,
                onAlarmEdit = onAlarmEdit,
                modifier = modifier
                    .clickable {
                        onAlarmClick(alarm)
                    }
            )
        },
        modifier = Modifier
            .padding(start = 32.dp, end = 24.dp)
    )
}

@Composable
private fun AlarmItem(
    alarm: AlarmModel,
    onAlarmEdit: (AlarmModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = alarm.time,
            fontSize = 32.sp,
            color = OnTertiaryLight
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = alarm.isActivated,
            onCheckedChange = {
                onAlarmEdit(
                    alarm.copy(
                        isActivated = it
                    )
                )
            },
            colors = SwitchDefaults.colors(
                checkedTrackColor = ConstraintLight,
                uncheckedTrackColor = InverseOnSurfaceLight
            )
        )
    }
}

private fun detectDayOfWeekFromText(text: String): DayOfWeek? {
    val lower = text.lowercase()

    return when {
        "monday" in lower -> DayOfWeek.MONDAY
        "tuesday" in lower -> DayOfWeek.TUESDAY
        "wednesday" in lower -> DayOfWeek.WEDNESDAY
        "thursday" in lower -> DayOfWeek.THURSDAY
        "friday" in lower -> DayOfWeek.FRIDAY
        "saturday" in lower -> DayOfWeek.SATURDAY
        "sunday" in lower -> DayOfWeek.SUNDAY
        else -> null
    }
}