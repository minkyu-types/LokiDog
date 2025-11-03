package dev.loki.dog.feature.temp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.loki.dog.component.AlarmGroupSwipeToDeleteItem
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.theme.OnPrimaryContainerLight
import dev.loki.dog.theme.OnTertiaryLight
import lokidog.composeapp.generated.resources.Res
import lokidog.composeapp.generated.resources.save_temp
import org.jetbrains.compose.resources.stringResource

@Composable
fun TempAlarmGroupsScreen(
    viewModel: TempAlarmGroupsViewModel,
    onBack: () -> Unit,
    onAlarmGroupClick: (AlarmGroupModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->

        }
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
                onBack = onBack,
            )
        }

        itemsIndexed(
            items = state.tempAlarmGroupList,
            key = { _, alarm -> alarm.id }
        ) { index, item ->
            AlarmGroupSwipeToDeleteItem(
                alarmGroup = item,
                onAlarmGroupClick = { group ->
                    onAlarmGroupClick(group)
                },
                onDelete = { group ->
                    viewModel.deleteTempAlarmGroup(group)
                }
            )

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
private fun TempAlarmGroupTopBar(
    onBack: () -> Unit,
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
            text = stringResource(Res.string.save_temp),
            fontSize = 20.sp,
            color = OnTertiaryLight,
            modifier = modifier
                .padding(start = 12.dp, end = 4.dp, bottom = 2.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}