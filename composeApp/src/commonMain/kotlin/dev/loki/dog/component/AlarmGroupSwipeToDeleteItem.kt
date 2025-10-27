package dev.loki.dog.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowRight
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.theme.OnTertiaryLight

@Composable
fun AlarmGroupSwipeToDeleteItem(
    alarmGroup: AlarmGroupModel,
    onAlarmGroupClick: (AlarmGroupModel) -> Unit,
    onActivationChange: ((AlarmGroupModel) -> Unit)? = null,
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
                    .fillMaxWidth()
            ) {
                AlarmGroupItem(
                    alarmGroup = alarmGroup,
                    switchEnabled = true,
                    onActivationChange = {
                        onActivationChange?.invoke(it)
                    },
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Sharp.ArrowRight,
                    contentDescription = null,
                    tint = OnTertiaryLight,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        modifier = modifier
            .padding(end = 16.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onAlarmGroupClick(alarmGroup)
            }
            .padding(start = 24.dp, end = 8.dp)
    )
}