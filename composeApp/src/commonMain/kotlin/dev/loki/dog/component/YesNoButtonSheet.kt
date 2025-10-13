package dev.loki.dog.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * @param onDismissRequest - Executes when the user clicks outside of the bottom sheet, after sheet animates to Hidden.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YesNoBottomSheet(
    yesNoText: Pair<String, String>,
    onYesClick: () -> Unit,
    onNoClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = {

        },
        modifier = modifier
    ) {
        content()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 16.dp)
        ) {
            Button(
                onClick = {
                    onNoClick()
                },
            ) {
                Text(
                    text = yesNoText.second,
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = {
                    onYesClick()
                },
            ) {
                Text(
                    text = yesNoText.first,
                    fontSize = 18.sp
                )
            }
        }
    }
}