package dev.loki.dog.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.loki.dog.theme.ConstraintLight
import dev.loki.dog.theme.OnPrimaryContainerLight

@Composable
fun <T> SelectionModeItem(
    item: T,
    isChecked: Boolean,
    onCheckedChange: (T, Boolean) -> Unit,
    checkedBoxColor: Color = ConstraintLight,
    uncheckedBoxColor: Color = OnPrimaryContainerLight,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { isChecked ->
                onCheckedChange(item, isChecked)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = checkedBoxColor,
                uncheckedColor = uncheckedBoxColor
            ),
            modifier = Modifier.padding(end = 8.dp)
        )
        content()
    }
}