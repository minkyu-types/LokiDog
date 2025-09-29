package dev.loki.dog

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import dev.loki.dog.feature.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
internal fun App(
    navController: NavHostController = rememberNavController()
) = MaterialTheme {

    MainScreen(
        navController = navController
    )
}
