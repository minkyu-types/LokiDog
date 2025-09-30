package dev.loki.dog.feature

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.loki.dog.feature.main.AlarmMainScreen
import dev.loki.dog.feature.main.AlarmMainViewModel
import dev.loki.dog.feature.temp.TempAlarmGroupsScreen
import dev.loki.dog.feature.temp.TempAlarmGroupsViewModel
import org.koin.mp.KoinPlatform.getKoin

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
    var isSelectionMode by remember { mutableStateOf(false) }

    Scaffold(
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
            if (currentScreen == MainScreens.ALARM_MAIN) {
                MainFabMenu(
                    isSelectionMode = isSelectionMode,
                    onAddClick = {

                    },
                    onActivateSelectionClick = {
                        isSelectionMode = it
                    }
                )
            }
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
                    val viewModel = getKoin().get<AlarmMainViewModel>()
                    AlarmMainScreen(
                        isSelectionMode = isSelectionMode,
                        viewModel = viewModel,
                        onAlarmGroupClick = { group ->

                        }
                    )
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
                    val viewModel = getKoin().get<TempAlarmGroupsViewModel>()
                    TempAlarmGroupsScreen(
                        viewModel = viewModel,
                        onAlarmGroupClick = {
                            navController.navigate(SubScreens.ALARM_GROUP_DETAIL.name)
                        }
                    )
                }

            }
        }
    }
}

@Composable
private fun MainFabMenu(
    isSelectionMode: Boolean,
    onAddClick: () -> Unit,
    onActivateSelectionClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            AnimatedVisibility(visible = expanded) {
                FloatingActionButton(
                    onClick = { onActivateSelectionClick(!isSelectionMode) },
                    containerColor = Color.Cyan
                ) {
                    Icon(
                        imageVector = if (isSelectionMode) Icons.Default.EditOff else Icons.Default.Edit,
                        contentDescription = "Selection mode on",
                        tint = Color.White
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                FloatingActionButton(
                    onClick = { onAddClick() },
                    containerColor = Color.Cyan
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add alarm group",
                        tint = Color.White
                    )
                }
            }

            FloatingActionButton(
                onClick = { expanded = !expanded },
                containerColor = Color.Cyan
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.Close else Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
        }
    }
}
