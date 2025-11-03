package dev.loki.dog.feature

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.savedstate.read
import dev.loki.dog.feature.addalarmgroup.AddAlarmGroupScreen
import dev.loki.dog.feature.addalarmgroup.TempAlarmTimeGenerator
import dev.loki.dog.feature.main.AlarmMainScreen
import dev.loki.dog.feature.main.AlarmMainViewModel
import dev.loki.dog.feature.temp.TempAlarmGroupsScreen
import dev.loki.dog.feature.temp.TempAlarmGroupsViewModel
import dev.loki.dog.feature.timer.TimerScreen
import dev.loki.dog.feature.timer.TimerViewModel
import dev.loki.dog.theme.OnPrimaryContainerLight
import dev.loki.dog.theme.OnTertiaryLight
import dev.loki.dog.theme.PrimaryLight
import dev.loki.dog.theme.Seed
import kotlinx.coroutines.launch
import lokidog.composeapp.generated.resources.Res
import lokidog.composeapp.generated.resources.bottom_bar_alarm_group_list
import lokidog.composeapp.generated.resources.bottom_bar_alarm_timer
import lokidog.composeapp.generated.resources.screen_sub_alarm_group_add
import lokidog.composeapp.generated.resources.screen_sub_alarm_group_list_temp
import org.jetbrains.compose.resources.stringResource
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    val backstackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backstackEntry?.destination?.route
    val currentScreen: Screens = when {
        currentRoute == MainScreen.ALARM.name -> MainScreen.ALARM
        currentRoute == MainScreen.TIMER.name -> MainScreen.TIMER
        currentRoute == SubScreen.ALARM_GROUP_TEMP_LIST.name -> SubScreen.ALARM_GROUP_TEMP_LIST
        currentRoute?.startsWith(SubScreen.ALARM_GROUP_ADD.name) == true -> SubScreen.ALARM_GROUP_ADD
        else -> MainScreen.ALARM
    }
    val mainScreens = listOf(MainScreen.ALARM, MainScreen.TIMER)
    val showBottomBar = (currentScreen in mainScreens)

    var alarmGroupSize by remember { mutableStateOf(0) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectAll by remember { mutableStateOf(false) }
    var deleteSelected by remember { mutableStateOf(false) }
    var tempAlarmGroupSize by remember { mutableStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
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
                            onClick = { navController.navigate(tab.name) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    text = tab.getTitle(),
                                    color = OnTertiaryLight
                                )
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentScreen == MainScreen.ALARM) {
                MainFabMenu(
                    isSelectionMode = isSelectionMode,
                    allItemsSelected = selectAll,
                    tempAlarmGroupSize = tempAlarmGroupSize,
                    onTempClick = {
                        navController.navigate(SubScreen.ALARM_GROUP_TEMP_LIST.name)
                    },
                    onAddClick = {
                        navController.navigate("${SubScreen.ALARM_GROUP_ADD.name}/${0L}")
                    },
                    onSelectAllClick = {
                        selectAll = !selectAll
                    },
                    onActivateSelectionClick = {
                        isSelectionMode = it
                        selectAll = false
                    },
                    onDeleteSelectedClick = {
                        deleteSelected = true
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
                    navController.navigate(SubScreen.ALARM_GROUP_TEMP_LIST.name)
                }
        ) {
            NavHost(
                navController = navController,
                startDestination = MainScreen.ALARM.name
            ) {
                composable(
                    route = MainScreen.ALARM.name
                ) {
                    val viewModel = getKoin().get<AlarmMainViewModel>()
                    AlarmMainScreen(
                        isSelectionMode = isSelectionMode,
                        selectAll = selectAll,
                        deleteSelectedItems = deleteSelected,
                        viewModel = viewModel,
                        onDeleteComplete = {
                            deleteSelected = false
                        },
                        onSelectedItemsChange = { items ->
                            if (items.isEmpty()) {
                                selectAll = false
                            }
                            if (items.size == alarmGroupSize) {
                                selectAll = true
                            }
                        },
                        onAlarmGroupUpdate = { items ->
                            alarmGroupSize = items.size
                        },
                        onTempSizeUpdate = { size ->
                            tempAlarmGroupSize = size
                        },
                        onAlarmGroupClick = { group ->
                            navController.navigate("${SubScreen.ALARM_GROUP_ADD.name}/${group.id}")
                        },
                        onAddAlarmGroupClick = {
                            navController.navigate("${SubScreen.ALARM_GROUP_ADD.name}/${0L}")
                        }
                    )
                }

                composable(
                    route = MainScreen.TIMER.name
                ) {
                    TimerScreen(
                        viewModel = getKoin().get<TimerViewModel>()
                    )
                }

                composable(
                    route = "${SubScreen.ALARM_GROUP_ADD.name}/{groupId}",
                    arguments = listOf(navArgument("groupId") { type = NavType.LongType })
                ) { backstackEntry ->
                    AddAlarmGroupScreen(
                        groupId = backstackEntry.arguments?.read { getLong("groupId") } ?: 0L,
                        onSaveOrSaveTemp = {
                            TempAlarmTimeGenerator.reset()
                            navController.popBackStack()
                        },
                        onError = { message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }
                    )
                }

                composable(
                    route = SubScreen.ALARM_GROUP_TEMP_LIST.name
                ) {
                    val viewModel = getKoin().get<TempAlarmGroupsViewModel>()
                    TempAlarmGroupsScreen(
                        viewModel = viewModel,
                        onBack = { navController.popBackStack() },
                        onAlarmGroupClick = { group ->
                            navController.navigate("${SubScreen.ALARM_GROUP_ADD.name}/${group.id}")
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
    allItemsSelected: Boolean,
    tempAlarmGroupSize: Int,
    onTempClick: () -> Unit,
    onAddClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onActivateSelectionClick: (Boolean) -> Unit,
    onDeleteSelectedClick: () -> Unit,
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
            modifier = Modifier.padding(12.dp)
        ) {
            AnimatedVisibility(visible = (expanded && (tempAlarmGroupSize != 0))) {
                FloatingActionButton(
                    onClick = {
                        onTempClick()
                        expanded = false
                    },
                    containerColor = PrimaryLight
                ) {
                    Box(
                        contentAlignment = Alignment.TopEnd
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dataset,
                            contentDescription = "Selection mode on",
                            tint = Color.White
                        )

                        Box(
                            modifier = Modifier
                                .offset(x = 8.dp, y = (-6).dp)
                                .size(20.dp)
                                .background(Color.Red, shape = CircleShape)
                                .border(1.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tempAlarmGroupSize.toString(),
                                color = Color.White,
                                fontSize = 12.sp,
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(visible = expanded && !isSelectionMode) {
                FloatingActionButton(
                    onClick = {
                        onActivateSelectionClick(!isSelectionMode)
                        expanded = false
                    },
                    containerColor = PrimaryLight
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
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

            if (isSelectionMode) {
                FloatingActionButton(
                    onClick = {
                        onDeleteSelectedClick()
                    },
                    containerColor = PrimaryLight
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete selected alarm group",
                        tint = Color.White
                    )
                }
                FloatingActionButton(
                    onClick = {
                        onSelectAllClick()
                    },
                    containerColor = PrimaryLight
                ) {
                    Icon(
                        imageVector = if (allItemsSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Check or Uncheck alarm group",
                        tint = Color.White
                    )
                }
                FloatingActionButton(
                    onClick = {
                        onActivateSelectionClick(!isSelectionMode)
                        expanded = false
                    },
                    containerColor = PrimaryLight
                ) {
                    Icon(
                        imageVector = Icons.Default.EditOff,
                        contentDescription = "Selection mode off",
                        tint = Color.White
                    )
                }
            } else {
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
}

@Composable
fun MainScreen.getTitle(): String {
    return when (this) {
        MainScreen.ALARM -> stringResource(Res.string.bottom_bar_alarm_group_list)
        MainScreen.TIMER -> stringResource(Res.string.bottom_bar_alarm_timer)
    }
}

@Composable
fun SubScreen.getTitle(): String {
    return when (this) {
        SubScreen.ALARM_GROUP_ADD -> stringResource(Res.string.screen_sub_alarm_group_add)
        SubScreen.ALARM_GROUP_TEMP_LIST -> stringResource(Res.string.screen_sub_alarm_group_list_temp)
    }
}