package dev.loki.dog.feature

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import dev.loki.dog.feature.addalarmgroup.AddAlarmGroupScreen
import dev.loki.dog.feature.addalarmgroup.AddAlarmGroupViewModel
import dev.loki.dog.feature.main.AlarmMainScreen
import dev.loki.dog.feature.main.AlarmMainViewModel
import dev.loki.dog.feature.temp.TempAlarmGroupsScreen
import dev.loki.dog.feature.temp.TempAlarmGroupsViewModel
import dev.loki.dog.theme.OnPrimaryContainerLight
import dev.loki.dog.theme.OnTertiaryLight
import dev.loki.dog.theme.PrimaryLight
import dev.loki.dog.theme.Seed
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    val backstackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backstackEntry?.destination?.route
    val currentScreen: Screens = when (currentRoute) {
        MainScreen.AlarmMain.title -> MainScreen.AlarmMain
        MainScreen.TimerMain.title -> MainScreen.TimerMain
        SubScreen.AlarmGroupAdd.title -> SubScreen.AlarmGroupAdd
        SubScreen.AlarmGroupDetail.title -> SubScreen.AlarmGroupDetail
        SubScreen.TempAlarmGroupList.title -> SubScreen.TempAlarmGroupList
        else -> MainScreen.AlarmMain
    }
    val mainScreens = listOf(MainScreen.AlarmMain, MainScreen.TimerMain)
    val showBottomBar = (currentScreen in mainScreens)
    var isSelectionMode by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = OnPrimaryContainerLight
                ) {
                    mainScreens.forEach { tab ->
                        NavigationBarItem(
                            selected = currentScreen == tab,
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = OnTertiaryLight
                            ),
                            onClick = { navController.navigate(tab.title) },
                            icon = {

                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    color = OnTertiaryLight
                                )
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentScreen == MainScreen.AlarmMain) {
                MainFabMenu(
                    isSelectionMode = isSelectionMode,
                    onAddClick = {
                        navController.navigate(SubScreen.AlarmGroupAdd.title)
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
                .background(color = Seed)
                .fillMaxSize()
                .padding(innerPadding)
                .clickable {
                    navController.navigate(SubScreen.TempAlarmGroupList.title)
                }
        ) {
            NavHost(
                navController = navController,
                startDestination = MainScreen.AlarmMain.title
            ) {
                composable(
                    route = MainScreen.AlarmMain.title
                ) {
                    val viewModel = getKoin().get<AlarmMainViewModel>()
                    AlarmMainScreen(
                        isSelectionMode = isSelectionMode,
                        viewModel = viewModel,
                        onAlarmGroupClick = { group ->
                            navController.navigate(SubScreen.AlarmGroupDetail.title)
                        }
                    )
                }

                composable(
                    route = MainScreen.TimerMain.title
                ) {

                }

                composable(
                    route = SubScreen.AlarmGroupAdd.title
                ) {
                    val viewModel = getKoin().get<AddAlarmGroupViewModel>()
                    AddAlarmGroupScreen(
                        viewModel = viewModel,
                        onSaveOrSaveTemp = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(
                    route = SubScreen.AlarmGroupDetail.title
                ) {

                }

                composable(
                    route = SubScreen.TempAlarmGroupList.title
                ) {
                    val viewModel = getKoin().get<TempAlarmGroupsViewModel>()
                    TempAlarmGroupsScreen(
                        viewModel = viewModel,
                        onAlarmGroupClick = {
                            navController.navigate(SubScreen.AlarmGroupDetail.title)
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
                    onClick = {
                        onActivateSelectionClick(!isSelectionMode)
                        expanded = false
                    },
                    containerColor = PrimaryLight
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
                    onClick = {
                        onAddClick()
                        expanded = false
                    },
                    containerColor = PrimaryLight
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
                containerColor = PrimaryLight
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
