package dev.loki.dog

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.loki.dog.expect.AlarmDialogContainer
import dev.loki.dog.feature.AuthScreen
import dev.loki.dog.feature.MainScreen
import dev.loki.dog.feature.login.LoginScreen
import dev.loki.dog.feature.login.LoginViewModel
import dev.loki.dog.feature.main.AlarmMainViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.mp.KoinPlatform.getKoin

@Preview
@Composable
internal fun App(
    navController: NavHostController = rememberNavController()
) = MaterialTheme {
    Box {
        NavHost(
            navController = navController,
            startDestination = AuthScreen.LOGIN.name
        ) {
            composable(route = AuthScreen.LOGIN.name) {
                val viewModel = getKoin().get<LoginViewModel>()
                LoginScreen(
                    viewModel = viewModel,
                    onNavigateToMain = {
                        navController.navigate("main") {
                            popUpTo(AuthScreen.LOGIN.name) { inclusive = true }
                        }
                    }
                )
            }

            composable(route = "main") {
                MainScreen(
                    onSignOut = {
                        navController.navigate(AuthScreen.LOGIN.name) {
                            popUpTo("main") { inclusive = true }
                        }
                    }
                )
            }
        }

        // iOS용 알람 다이얼로그 (Android는 별도 Activity 사용)
        AlarmDialogContainer()
    }
}
