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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Delete
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
import co.touchlab.kermit.Logger
import dev.loki.dog.component.SelectionModeItem
import dev.loki.dog.component.SwipeToDeleteItem
import dev.loki.dog.component.TimeWheelBottomSheet
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.model.AlarmModel
import dev.loki.dog.theme.ConstraintLight
import dev.loki.dog.theme.ErrorLighter
import dev.loki.dog.theme.InverseOnSurfaceLight
import dev.loki.dog.theme.OnPrimaryContainerLight
import dev.loki.dog.theme.OnTertiaryLight
import dev.loki.dog.theme.OutlineLight
import dev.loki.dog.theme.OutlineVariantLight
import dev.loki.dog.theme.PrimaryContainerLight
import dev.loki.dog.theme.TertiaryContainerLight
import kotlinx.datetime.DayOfWeek
import lokidog.composeapp.generated.resources.Res
import lokidog.composeapp.generated.resources.alarms
import lokidog.composeapp.generated.resources.alarms_max_description
import lokidog.composeapp.generated.resources.alarms_min_repeat_days
import lokidog.composeapp.generated.resources.save
import org.jetbrains.compose.resources.stringResource
import org.koin.mp.KoinPlatform.getKoin

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddAlarmGroupScreen(
    groupId: Long,
    onSaveOrSaveTemp: () -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = getKoin().get<AddAlarmGroupViewModel>()
    var isSelectionMode by remember { mutableStateOf(false) }
    val selectedAlarmIndexes = remember { mutableStateListOf<Int>() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    var alarmGroup by remember { mutableStateOf(AlarmGroupModel.createTemp()) }
    var alarms by remember { mutableStateOf(listOf<AlarmModel>()) }
    val focusManager = LocalFocusManager.current

    var showTimeBottomSheet by remember { mutableStateOf(false) }
    var timeBottomSheetData by remember { mutableStateOf<AlarmModel?>(null) }

    val keyOf = rememberStableKeyOf()

    if (showTimeBottomSheet) {
        TimeWheelBottomSheet(
            index = alarms.indexOf(timeBottomSheetData),
            alarm = timeBottomSheetData!!,
            onDismiss = {
                showTimeBottomSheet = false
            },
            onTimeChange = { index, updatedAlarm ->
                val prevAlarms = alarms
                val targetAlarm = prevAlarms[index]
                val newAlarms = prevAlarms.toMutableList() - targetAlarm + updatedAlarm
                alarms = newAlarms
                if (alarmGroup.id != 0L) {
                    viewModel.upsertAlarm(alarmGroup, updatedAlarm)
                }
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

    LaunchedEffect(state.alarmGroup) {
        alarmGroup = state.alarmGroup
    }

    LaunchedEffect(state.alarms) {
        alarms = state.alarms
        if (alarms.isEmpty() && isSelectionMode) {
            isSelectionMode = false
        }
    }

    Box(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {
        Column {
            AddAlarmGroupTopBar(
                alarmGroup = alarmGroup,
                alarms = alarms,
                onSaveOrSaveTemp = onSaveOrSaveTemp,
                onAddNewAlarm = { newAlarm ->
                    alarms += newAlarm
                    if (alarmGroup.id != 0L) {
                        viewModel.upsertAlarm(alarmGroup, newAlarm)
                    }
                },
                onSaveTempAlarmGroup = { alarmGroup ->
                    viewModel.saveTempAlarmGroup(alarmGroup, alarms)
                    onSaveOrSaveTemp()
                },
            )

            LazyColumn(
                modifier = modifier
                    .weight(1f)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        focusManager.clearFocus()
                    }
                    .padding(bottom = 16.dp)
            ) {
                item {
                    val repeatDayErrorStr = stringResource(Res.string.alarms_min_repeat_days)
                    Spacer(modifier = Modifier.height(16.dp))
                    EditableAlarmGroupHeader(
                        alarmGroup = alarmGroup,
                        onTitleChange = { title ->
                            alarmGroup = alarmGroup.copy(title = title)
                            if (!alarmGroup.isTemp) {
                                viewModel.saveAlarmGroup(alarmGroup, alarms)
                            }
                        },
                        onDescriptionChange = { description ->
                            alarmGroup = alarmGroup.copy(description = description)
                            if (!alarmGroup.isTemp) {
                                viewModel.saveAlarmGroup(alarmGroup, alarms)
                            }
                        },
                        onRepeatDaysChange = { repeatDays ->
                            if (repeatDays.size == 1 && alarmGroup.repeatDays == repeatDays) {
                                onError(repeatDayErrorStr)
                            }

                            alarmGroup = alarmGroup.copy(repeatDays = repeatDays)
                            if (!alarmGroup.isTemp) {
                                viewModel.saveAlarmGroup(alarmGroup, alarms)
                                viewModel.rescheduleAlarm(alarmGroup.isActivated, repeatDays, alarms)
                            }
                        },
                        onActivateChange = { isActivated ->
                            alarmGroup = alarmGroup.copy(isActivated = isActivated)
                            if (!alarmGroup.isTemp) {
                                viewModel.saveAlarmGroup(alarmGroup, alarms)
                                viewModel.rescheduleAlarm(alarmGroup.isActivated, alarmGroup.repeatDays, alarms)
                            }
                        },
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .padding(start = 32.dp, end = 24.dp)
                    ) {
                        Column {
                            Text(
                                text = stringResource(
                                    Res.string.alarms,
                                    alarms.size
                                ), // TODO(유료 구독 상태에 따라 최대 개수 조절)
                                color = OnTertiaryLight,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                            )
                            Text(
                                text = stringResource(Res.string.alarms_max_description, 10),
                                color = OutlineVariantLight,
                                fontSize = 14.sp,
                                modifier = Modifier
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        if (isSelectionMode && selectedAlarmIndexes.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    if (alarmGroup.isTemp) {
                                        alarms = alarms.filterIndexed { index, _ ->
                                            index !in selectedAlarmIndexes
                                        }
                                    } else {
                                        selectedAlarmIndexes.forEach { index ->
                                            val alarm = alarms[index]
                                            viewModel.deleteAlarm(alarm)
                                        }
                                    }
                                    selectedAlarmIndexes.clear()
                                },
                                modifier = Modifier
                                    .size(IconButtonDefaults.extraSmallContainerSize())
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = ErrorLighter
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        if (alarms.isNotEmpty()) {
                            IconButton(
                                enabled = alarms.isNotEmpty(),
                                onClick = {
                                    isSelectionMode = !isSelectionMode
                                    if (!isSelectionMode) {
                                        selectedAlarmIndexes.clear()
                                    }
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    contentColor = OnTertiaryLight,
                                    disabledContentColor = OutlineLight
                                ),
                                modifier = Modifier
                                    .size(IconButtonDefaults.extraSmallContainerSize())
                            ) {
                                Icon(
                                    imageVector = if (isSelectionMode) Icons.Default.EditOff else Icons.Default.Edit,
                                    contentDescription = null,
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                AlarmItemsSection(
                    alarms = alarms,
                    keyOf,
                    isSelectionMode = isSelectionMode,
                    selectedAlarmIndexes = selectedAlarmIndexes,
                    onAlarmEdit = { editedAlarm ->
                        val updatedAlarms = alarms.map { alarm ->
                            if (alarm.time == editedAlarm.time) editedAlarm else alarm
                        }
                        alarms = updatedAlarms

                        if (!alarmGroup.isTemp) {
                            viewModel.upsertAlarm(alarmGroup, editedAlarm)
                        }
                    },
                    onAlarmClick = {
                        showTimeBottomSheet = true
                        timeBottomSheetData = it
                    },
                    onAlarmDelete = { deleteAlarm ->
                        alarms = alarms - deleteAlarm
                        if (!alarmGroup.isTemp) {
                            viewModel.deleteAlarm(deleteAlarm)
                        }
                    },
                    onCheckedChange = { index ->
                        if (index in selectedAlarmIndexes) {
                            selectedAlarmIndexes.remove(index)
                        } else {
                            selectedAlarmIndexes.add(index)
                        }
                    }
                )
            }

            if (alarmGroup.isTemp) {
                Button(
                    enabled = (alarmGroup.title.isNotEmpty() && alarms.isNotEmpty() && alarmGroup.repeatDays.isNotEmpty()),
                    onClick = {
                        viewModel.saveAlarmGroup(alarmGroup, alarms)
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

private class IdGen {
    // 음수 ID를 사용하여 Room의 auto-generated ID(양수)와 충돌 방지
    private var next = -1L
    fun next() = next--
}

@Composable
private fun rememberStableKeyOf(): (AlarmModel) -> Any {
    // 임시 알람을 위한 UI 키 저장소
    val idGen = remember { IdGen() }
    val tempKeyMap = remember { mutableMapOf<String, Long>() }

    return remember {
        { alarm: AlarmModel ->
            if (alarm.id != 0L) {
                alarm.id                    // DB 알람 → PK 사용
            } else {
                // 임시 알람 → 내용 기반의 논리 키( time+memo )를 같은 아이템으로 간주
                // 최초 등장 시에만 증가 키를 부여해 "화면 생애 동안" 고정
                val logical = "t:${alarm.time}|${alarm.memo}"
                tempKeyMap.getOrPut(logical) { idGen.next() }
            }
        }
    }
}

private fun LazyListScope.AlarmItemsSection(
    alarms: List<AlarmModel>,
    keyOf: (AlarmModel) -> Any,
    isSelectionMode: Boolean,
    selectedAlarmIndexes: List<Int>,
    onAlarmEdit: (AlarmModel) -> Unit,
    onAlarmClick: (AlarmModel) -> Unit,
    onAlarmDelete: ((AlarmModel) -> Unit)?,
    onCheckedChange: (index: Int) -> Unit,
) {
    itemsIndexed(
        items = alarms.sortedBy {
            val time = it.time.split(":")
            time[0].toInt() * 60 + time[1].toInt()
        },
        key = { _, alarm ->
            keyOf(alarm).also {
                Logger.d { "qqqq KEY: $it" }
            }
        }
    ) { index, alarm ->
        if (isSelectionMode) {
            SelectableEditableAlarmItem(
                isChecked = (index in selectedAlarmIndexes),
                alarm = alarm,
                onAlarmEdit = { editedAlarm ->
                    onAlarmEdit(editedAlarm)
                },
                onCheckedChange = {
                    onCheckedChange(index)
                }
            )
        } else {
            EditableAlarmItem(
                alarm = alarm,
                onAlarmEdit = { editedAlarm ->
                    onAlarmEdit(editedAlarm)
                },
                onAlarmClick = {
                    onAlarmClick(alarm)
                },
                onAlarmDelete = { deleteAlarm ->
                    onAlarmDelete?.invoke(deleteAlarm)
                },
            )
        }

        if (index < alarms.lastIndex) {
            HorizontalDivider(
                color = OnPrimaryContainerLight,
                thickness = 1.dp,
                modifier = Modifier
                    .padding(horizontal = 32.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AddAlarmGroupTopBar(
    alarmGroup: AlarmGroupModel,
    alarms: List<AlarmModel>,
    onSaveOrSaveTemp: () -> Unit,
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
            Spacer(modifier = Modifier.width(16.dp))
        }

        IconButton(
            enabled = alarmGroup.title.isNotBlank() && alarms.size < 10,
            onClick = {
                val newAlarm = AlarmModel.createTemp(alarmGroup.id, alarms.lastOrNull())
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
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onRepeatDaysChange: (Set<DayOfWeek>) -> Unit,
    onActivateChange: (Boolean) -> Unit,
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

    Box {
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
                        onTitleChange(localTitle)
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 24.sp
                    ),
                    placeholder = {
                        Text(
                            text = "Title",
                            fontSize = 24.sp,
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
                        onActivateChange(it)
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
                    if (dayOfWeek in localRepeatDayOfWeeks && localRepeatDayOfWeeks.size == 1) {
                        onRepeatDaysChange(localRepeatDayOfWeeks)
                        return@DayOfWeekLazyRow
                    }

                    if (dayOfWeek in localRepeatDayOfWeeks) {
                        localRepeatDayOfWeeks -= dayOfWeek
                    } else {
                        localRepeatDayOfWeeks += dayOfWeek
                    }
                    onRepeatDaysChange(localRepeatDayOfWeeks)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = localDescription,
                onValueChange = {
                    localDescription = it
                    onDescriptionChange(localDescription)
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
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    SelectionModeItem(
        item = alarm,
        isChecked = isChecked,
        onCheckedChange = { _, _ ->

        },
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onCheckedChange()
            }
            .padding(start = 32.dp, end = 24.dp)
    ) {
        AlarmItem(
            isSelectionMode = true,
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
                isSelectionMode = false,
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
    isSelectionMode: Boolean,
    alarm: AlarmModel,
    onAlarmEdit: (AlarmModel) -> Unit,
    modifier: Modifier = Modifier
) {
    Logger.d { "Alarm: $alarm" }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
                .padding(end = 32.dp)
        ) {
            Text(
                text = alarm.time,
                fontSize = 32.sp,
                color = OnTertiaryLight
            )
            if (alarm.memo.isNotBlank()) {
                Text(
                    text = alarm.memo,
                    fontSize = 20.sp,
                    color = TertiaryContainerLight,
                    maxLines = 1
                )
            }
        }
        Switch(
            enabled = !isSelectionMode,
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
                disabledCheckedTrackColor = ConstraintLight,
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