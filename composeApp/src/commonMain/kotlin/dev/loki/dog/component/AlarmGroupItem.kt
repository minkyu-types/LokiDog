package dev.loki.dog.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.theme.ConstraintLight
import dev.loki.dog.theme.InverseOnSurfaceLight
import dev.loki.dog.theme.OnTertiaryLight
import dev.loki.dog.theme.PrimaryContainerLight
import lokidog.composeapp.generated.resources.Res
import lokidog.composeapp.generated.resources.alarms
import org.jetbrains.compose.resources.stringResource

@Composable
fun AlarmGroupItem(
    alarmGroup: AlarmGroupModel,
    onActivationChange: ((AlarmGroupModel) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .weight(1f)
                .padding(end = 32.dp)
        ) {
            Text(
                text = alarmGroup.title,
                fontSize = 24.sp,
                color = OnTertiaryLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(Res.string.alarms, alarmGroup.alarmSize),
                fontSize = 16.sp,
                color = PrimaryContainerLight
            )
        }

        Switch(
            checked = alarmGroup.isActivated,
            onCheckedChange = {
                onActivationChange?.invoke(alarmGroup.copy(isActivated = it))
            },
            colors = SwitchDefaults.colors(
                checkedTrackColor = ConstraintLight,
                uncheckedTrackColor = InverseOnSurfaceLight
            ),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}