package dev.loki.dog.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.loki.dog.model.AlarmGroupModel
import dev.loki.dog.theme.ConstraintLight
import dev.loki.dog.theme.InverseOnSurfaceLight
import dev.loki.dog.theme.OnTertiaryLight

@Composable
fun AlarmGroupItem(
    alarmGroup: AlarmGroupModel,
    onActivationChange: ((AlarmGroupModel) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = 20.dp)
    ) {
        Text(
            text = alarmGroup.title,
            fontSize = 28.sp,
            color = OnTertiaryLight
        )
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = alarmGroup.isActivated,
            onCheckedChange = {
                onActivationChange?.invoke(alarmGroup.copy(isActivated = it))
            },
            colors = SwitchDefaults.colors(
                checkedTrackColor = ConstraintLight,
                uncheckedTrackColor = InverseOnSurfaceLight
            )
        )
    }
}