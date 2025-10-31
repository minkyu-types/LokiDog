package dev.loki.dog

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dev.loki.dog.expect.AlarmDialogContainer
import dev.loki.dog.feature.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
internal fun App(
    navController: NavHostController = rememberNavController()
) = MaterialTheme {
    Box {
        MainScreen(
            navController = navController
        )

        // iOS용 알람 다이얼로그 (Android는 별도 Activity 사용)
        AlarmDialogContainer()
    }
}
