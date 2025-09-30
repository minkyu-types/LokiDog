package dev.loki.dog.feature.temp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.loki.dog.component.GlassPanel
import dev.loki.dog.feature.base.LoadState
import dev.loki.dog.model.AlarmGroupModel

@Composable
fun TempAlarmGroupsScreen(
    viewModel: TempAlarmGroupsViewModel,
    onAlarmGroupClick: (AlarmGroupModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is TempAlarmGroupsSideEffect.ShowUpdateDialog -> {

                }

                is TempAlarmGroupsSideEffect.ShowDeleteDialog -> {

                }
            }
        }
    }

    when (state.value.loadState) {
        is LoadState.Loading -> {

        }

        is LoadState.Success -> {
            val groups = (state.value.tempAlarmGroupList)

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(
                        items = groups,
                        key = { it.id }
                    ) { group ->
                        TempAlarmGroupItem(
                            alarmGroup = group,
                            onClick = onAlarmGroupClick
                        )
                    }
                }
                if (groups.isEmpty()) {
                    Text(
                        text = "알람을 추가해보세요"
                    )
                }
            }
        }

        else -> {

        }
    }

}

@Composable
private fun TempAlarmGroupItem(
    alarmGroup: AlarmGroupModel,
    onClick: (AlarmGroupModel) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassPanel {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .clickable {
                    onClick(alarmGroup)
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
}