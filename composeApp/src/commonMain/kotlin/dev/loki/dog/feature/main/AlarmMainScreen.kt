package dev.loki.dog.feature.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.loki.alarmgroup.model.AlarmMainSort
import dev.loki.dog.component.AlarmGroupSelectionModeItem
import dev.loki.dog.component.AlarmGroupSwipeToDeleteItem
import dev.loki.dog.handler.DraggableItem
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.theme.ConstraintLight
import dev.loki.dog.theme.OnPrimaryContainerLight
import dev.loki.dog.theme.OnTertiaryLight
import dev.loki.dog.theme.PrimaryLight
import dev.loki.dog.theme.Seed
import dev.loki.dog.theme.SurfaceVariantLight
import kotlinx.coroutines.launch
import lokidog.composeapp.generated.resources.Res
import lokidog.composeapp.generated.resources.sort_activated_first
import lokidog.composeapp.generated.resources.sort_alphabetical
import lokidog.composeapp.generated.resources.sort_custom
import lokidog.composeapp.generated.resources.sort_most_recent_created
import lokidog.composeapp.generated.resources.sort_most_recent_updated
import lokidog.composeapp.generated.resources.sort_title
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmMainScreen(
    isSelectionMode: Boolean,
    selectAll: Boolean,
    deleteSelectedItems: Boolean,
    viewModel: AlarmMainViewModel,
    onDeleteComplete: () -> Unit,
    onTempSizeUpdate: (Int) -> Unit,
    onAlarmGroupClick: (AlarmGroupModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val state by viewModel.state.collectAsStateWithLifecycle()
    var alarmGroups by remember { mutableStateOf(emptyList<AlarmGroupModel>()) }
    val selectedItems: MutableSet<AlarmGroupModel> = remember { mutableStateSetOf() }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSortBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(deleteSelectedItems) {
        if (selectedItems.isNotEmpty()) {
            viewModel.deleteSelectedAlarmGroups(selectedItems.toList())
            selectedItems.clear()
            onDeleteComplete()
        }
    }

    LaunchedEffect(isSelectionMode) {
        selectedItems.clear()
    }

    LaunchedEffect(state.tempAlarmSize) {
        onTempSizeUpdate(state.tempAlarmSize)
    }

    LaunchedEffect(state.alarmGroupList) {
        alarmGroups = state.alarmGroupList
    }

    LaunchedEffect(selectAll) {
        if (selectAll) {
            selectedItems.addAll(state.alarmGroupList)
        } else {
            selectedItems.clear()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AlarmMainSideEffect.ShowSortBottomSheet -> {
                    showSortBottomSheet = true
                }
            }
        }
    }

    if (showSortBottomSheet) {
        SortBottomSheet(
            sheetState = sheetState,
            prevSort = state.sort,
            onDismiss = {
                showSortBottomSheet = false
            },
            onSortChange = { sort ->
                viewModel.updateSort(sort)
                showSortBottomSheet = false
            },
        )
    }

    var draggingItem by remember { mutableStateOf<AlarmGroupModel?>(null) }
    var dragOffsetY by remember { mutableStateOf(0f) }

    if (alarmGroups.isEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    focusManager.clearFocus()
                }
        ) {
            Text(
                text = "알람을 추가해보세요",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = OnTertiaryLight
            )
        }
    } else {
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
                AlarmGroupTopBar(
                    currentSort = state.sort,
                    onClick = {
                        viewModel.showSortBottomSheet(state.sort)
                    },
                    onSettingClick = {

                    }
                )
            }

            itemsIndexed(
                items = alarmGroups,
                key = { _, alarm -> alarm.id }
            ) { index, item ->
                if (isSelectionMode) {
                    val isDragging = (item == draggingItem)

                    AlarmGroupSelectionModeItem(
                        index = index,
                        isChecked = (item in selectedItems),
                        alarmGroup = item,
                        onCheckedChange = { group, isChecked ->
                            if (isChecked) {
                                selectedItems.add(group)
                            } else {
                                selectedItems.remove(group)
                            }
                        },
                        dragController = object : DraggableItem {
                            override fun onDragStart(index: Int) {
                                draggingItem = item
                            }

                            override fun onDrag(dragAmount: Float, itemHeight: Float) {
                                dragOffsetY += dragAmount

                                val currentItem = draggingItem ?: return
                                val currentIndex = alarmGroups.indexOf(currentItem)
                                val targetIndex = calculateNewIndex(
                                    currentIndex = currentIndex,
                                    offsetY = dragOffsetY,
                                    listSize = alarmGroups.size,
                                    itemHeight = itemHeight
                                )

                                if (targetIndex != null && targetIndex != currentIndex) {
                                    alarmGroups = alarmGroups.toMutableList().apply {
                                        val from = currentIndex.coerceIn(0, lastIndex)
                                        val to = targetIndex.coerceIn(0, lastIndex)
                                        if (from != to) {
                                            val moved = removeAt(from)
                                            add(to, moved)
                                        }
                                    }
                                    dragOffsetY = 0f
                                }
                            }

                            override fun onDragEnd() {
                                draggingItem = null
                                dragOffsetY = 0f
                            }

                            override fun onDragCancel() {
                                draggingItem = null
                                dragOffsetY = 0f
                            }
                        },
                        modifier = Modifier
                            .graphicsLayer {
                                translationY = if (isDragging) dragOffsetY else 0f
                                shadowElevation = if (isDragging) 8.dp.toPx() else 0f
                            }
                            .background(
                                color = if (isDragging) PrimaryLight else Color.Transparent
                            )
                    )
                } else {
                    AlarmGroupSwipeToDeleteItem(
                        alarmGroup = item,
                        onAlarmGroupClick = { group ->
                            onAlarmGroupClick(group)
                        },
                        onActivationChange = { group ->
                            viewModel.updateAlarmGroup(group)
                        },
                        onDelete = { group ->
                            viewModel.deleteAlarmGroup(group)
                        }
                    )
                }

                if (index < state.alarmGroupList.lastIndex) {
                    HorizontalDivider(
                        color = OnPrimaryContainerLight,
                        thickness = 1.dp,
                    )
                }
            }
        }
    }
}

private fun calculateNewIndex(
    currentIndex: Int,
    offsetY: Float,
    listSize: Int,
    itemHeight: Float
): Int? {
    val movedPositions = (offsetY / itemHeight).toInt()
    val newIndex = (currentIndex + movedPositions).coerceIn(0, listSize - 1)
    return if (newIndex != currentIndex) newIndex else null
}

@Composable
private fun AlarmGroupTopBar(
    currentSort: AlarmMainSort,
    onClick: () -> Unit,
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onClick()
            }
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clipToBounds()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(color = OnPrimaryContainerLight)
                .border(width = 1.dp, color = OnTertiaryLight, shape = RoundedCornerShape(16.dp))
                .padding(start = 12.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
        ) {
            Text(
                text = currentSort.getLabel(),
                fontSize = 18.sp,
                color = OnTertiaryLight,
                modifier = modifier
                    .padding(vertical = 4.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = OnTertiaryLight
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            tint = SurfaceVariantLight,
            modifier = Modifier
                .clickable {
                    onSettingClick()
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortBottomSheet(
    prevSort: AlarmMainSort,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSortChange: (AlarmMainSort) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onDismiss()
        },
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = OnTertiaryLight
            )
        },
        containerColor = Seed,
        modifier = modifier
    ) {
        Text(
            text = stringResource(Res.string.sort_title),
            fontSize = 20.sp,
            color = OnTertiaryLight,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 24.dp).background(OnPrimaryContainerLight)
        )
        Spacer(modifier = Modifier.height(24.dp))
        AlarmMainSort.entries.forEach { sort ->
            Text(
                text = sort.getLabel(),
                fontSize = 18.sp,
                color = if (prevSort == sort) ConstraintLight else OnTertiaryLight,
                fontWeight = if (prevSort == sort) FontWeight.SemiBold else null,
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        coroutineScope.launch {
                            onSortChange(sort)
                            sheetState.hide()
                            onDismiss()
                        }
                    }
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun AlarmMainSort.getLabel(): String {
    return when (this) {
        AlarmMainSort.MOST_RECENT_CREATED -> stringResource(Res.string.sort_most_recent_created)
        AlarmMainSort.MOST_RECENT_UPDATED -> stringResource(Res.string.sort_most_recent_updated)
        AlarmMainSort.ACTIVATED_FIRST -> stringResource(Res.string.sort_activated_first)
        AlarmMainSort.ALPHABETICAL -> stringResource(Res.string.sort_alphabetical)
        AlarmMainSort.CUSTOM -> stringResource(Res.string.sort_custom)
    }
}