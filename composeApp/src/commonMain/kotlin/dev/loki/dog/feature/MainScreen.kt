package dev.loki.dog.feature

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    val backstackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MainScreens.valueOf(
        backstackEntry?.destination?.route ?: MainScreens.ALARM_MAIN.name
    )
    val mainScreens = MainScreens.entries
    val showBottomBar = (currentScreen in mainScreens)

    Scaffold(
        topBar = {

        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    mainScreens.forEach { tab ->
                        NavigationBarItem(
                            selected = currentScreen == tab,
                            onClick = { navController.navigate(tab.name) },
                            icon = {

                            },
                            label = { Text(tab.title) }
                        )
                    }
                }
            }
        },
        floatingActionButton = {

        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = MainScreens.ALARM_MAIN.name
            ) {
                composable(
                    route = MainScreens.ALARM_MAIN.name
                ) {

                }
                composable(
                    route = MainScreens.TIMER_MAIN.name
                ) {

                }

                composable(
                    route = SubScreens.ALARM_GROUP_DETAIL.name
                ) {

                }

                composable(
                    route = SubScreens.TEMP_ALARM_GROUP_LIST.name
                ) {

                }

            }
        }
    }
}