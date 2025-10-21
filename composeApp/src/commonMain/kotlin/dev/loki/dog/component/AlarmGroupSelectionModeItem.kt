package dev.loki.dog.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import dev.loki.dog.handler.DraggableItem
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.theme.OnTertiaryLight

@Composable
fun AlarmGroupSelectionModeItem(
    index: Int,
    isChecked: Boolean,
    alarmGroup: AlarmGroupModel,
    onCheckedChange: (AlarmGroupModel, Boolean) -> Unit,
    dragController: DraggableItem? = null,
    modifier: Modifier = Modifier
) {
    var itemHeightPx by remember { mutableStateOf(0f) }

    SelectionModeItem(
        item = alarmGroup,
        isChecked = isChecked,
        onCheckedChange = { group, checked ->
            onCheckedChange(group, checked)
        },
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AlarmGroupItem(alarmGroup = alarmGroup)
                }

                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = OnTertiaryLight,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(end = 16.dp)
            .onGloballyPositioned { layoutCoordinates ->
                itemHeightPx = layoutCoordinates.size.height.toFloat()
            }
            .pointerInput(itemHeightPx) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        dragController?.onDragStart(index)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragController?.onDrag(dragAmount.y, itemHeightPx)
                    },
                    onDragEnd = {
                        dragController?.onDragEnd()
                    },
                    onDragCancel = {
                        dragController?.onDragCancel()
                    }
                )
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onCheckedChange(alarmGroup, !isChecked)
            }
            .padding(start = 24.dp, end = 8.dp)
    )
}