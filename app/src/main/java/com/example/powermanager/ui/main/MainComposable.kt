package com.example.powermanager.ui.main

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.powermanager.R
import com.example.powermanager.ui.model.PowerManagerAppModel
import com.example.powermanager.ui.navigation.navigationItems
import com.example.powermanager.ui.screens.main_screens.ControlScreen
import com.example.powermanager.ui.screens.main_screens.HomeScreen
import com.example.powermanager.ui.screens.main_screens.LiveChartsScreen
import com.example.powermanager.ui.screens.main_screens.RecordingScreen
import com.example.powermanager.ui.screens.main_screens.SettingsScreen
import com.example.powermanager.ui.screens.secondary_screens.CpuConfigurationFileInspectScreen
import com.example.powermanager.ui.screens.secondary_screens.CpuIdleExplanationScreen
import com.example.powermanager.ui.screens.secondary_screens.RecordingResultFileInspectScreen
import com.example.powermanager.ui.screens.secondary_screens.RecordingResultViewScreen
import com.example.powermanager.ui.screens.secondary_screens.RecordingResultsComparisonScreen
import com.example.powermanager.ui.screens.secondary_screens.ScalingGovernorsExplanationScreen
import com.example.powermanager.ui.screens.secondary_screens.UDFSScreen
import com.example.powermanager.utils.CONTROL_SCREEN_NAME
import com.example.powermanager.utils.CPUIDLE_EXPLANATION_SCREEN_NAME
import com.example.powermanager.utils.CPU_CONFIGURATION_INSPECT_SCREEN_NAME
import com.example.powermanager.utils.HOME_SCREEN_NAME
import com.example.powermanager.utils.LIVE_CHARTS_SCREEN_NAME
import com.example.powermanager.utils.RECORDING_RESULTS_COMPARISON_SCREEN_NAME
import com.example.powermanager.utils.RECORDING_RESULT_FILE_INSPECT_SCREEN_NAME
import com.example.powermanager.utils.RECORDING_RESULT_SCREEN_NAME
import com.example.powermanager.utils.RECORDING_SCREEN_NAME
import com.example.powermanager.utils.SCALING_GOVERNORS_EXPLANATION_SCREEN_NAME
import com.example.powermanager.utils.SETTINGS_SCREEN_NAME
import com.example.powermanager.utils.UDFS_SCREEN_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/*
 * The main composable of the app
 */
@Composable
fun PowerManagerApp(
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    model: PowerManagerAppModel,
    goToDisplaySettings: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                    navController = navController,
                    scope = scope,
                    drawerState = drawerState,
                )
            },
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        scope = scope,
                        drawerState = drawerState,
                    )
                }
            ) {
                ScreensNavHost(
                    navController = navController,
                    topPadding = it.calculateTopPadding(),
                    model = model,
                    goToDisplaySettings = goToDisplaySettings
                )
            }
        }
    }
}

/*
 * Navigation host that manages navigation between app screens (both main screens and secondary screen)
 */
@Composable
fun ScreensNavHost(
    navController: NavHostController,
    topPadding: Dp,
    model: PowerManagerAppModel,
    goToDisplaySettings: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = HOME_SCREEN_NAME
    ) {
        // home screen
        composable(
            route = HOME_SCREEN_NAME,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
        ) {
            HomeScreen(
                topPadding = topPadding,
                model = model,
                onGoToLiveChartsButtonClicked = {
                    navController.navigate(LIVE_CHARTS_SCREEN_NAME)
                },
                onGoToControlScreenButtonClicked = {
                    navController.navigate(CONTROL_SCREEN_NAME)
                }
            )
        }

        // statistics screen
        composable(
            route = LIVE_CHARTS_SCREEN_NAME,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
        ) {
            LiveChartsScreen(
                topPadding = topPadding,
                model = model,
            )
        }

        // control screen
        composable(
            route = CONTROL_SCREEN_NAME,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
        ) {
            ControlScreen(
                topPadding = topPadding,
                goToDisplaySettings = goToDisplaySettings,
                model = model,
                openScalingGovernorsExplanationScreen = {
                    navController.navigate(SCALING_GOVERNORS_EXPLANATION_SCREEN_NAME)
                },
                openCpuIdleExplanationScreen = {
                    navController.navigate(CPUIDLE_EXPLANATION_SCREEN_NAME)
                },
                openCpuConfigurationScreen = {
                    navController.navigate(CPU_CONFIGURATION_INSPECT_SCREEN_NAME)
                },
                openUDFSScreen = {
                    navController.navigate(UDFS_SCREEN_NAME)
                },
                goToAppSettings = {
                    navController.navigate(SETTINGS_SCREEN_NAME)
                }
            )
        }

        // recording screen
        composable(
            route = RECORDING_SCREEN_NAME,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
        ) {
            RecordingScreen(
                topPadding = topPadding,
                model = model,
                openRecordingResultViewScreen = {
                    navController.navigate(RECORDING_RESULT_SCREEN_NAME)
                },
                openRecordingResultFileInspectScreen = {
                    navController.navigate(RECORDING_RESULT_FILE_INSPECT_SCREEN_NAME)
                }
            )
        }

        // recording results view screen
        composable(
            route = RECORDING_RESULT_SCREEN_NAME,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
        ) {
            RecordingResultViewScreen(
                topPadding = topPadding,
                model = model,
                openRecordingResultsComparisonScreen = {
                    navController.navigate(RECORDING_RESULTS_COMPARISON_SCREEN_NAME)
                }
            )
        }

        // scaling governors details screen
        composable(
            route = SCALING_GOVERNORS_EXPLANATION_SCREEN_NAME,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
        ) {
            ScalingGovernorsExplanationScreen(
                topPadding = topPadding
            )
        }

        // Cpuidle details screen
        composable(
            route = CPUIDLE_EXPLANATION_SCREEN_NAME,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
        ) {
            CpuIdleExplanationScreen(
                topPadding = topPadding
            )
        }

        // UDFS screen
        composable(
            route = UDFS_SCREEN_NAME,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
        ) {
            UDFSScreen(
                topPadding = topPadding,
                model = model,
                openControlScreen = {
                    navController.navigate(CONTROL_SCREEN_NAME)
                }
            )
        }

        // cpu configuration file inspect screen
        composable(
            route = CPU_CONFIGURATION_INSPECT_SCREEN_NAME,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
        ) {
            CpuConfigurationFileInspectScreen(
                topPadding = topPadding,
                model = model
            )
        }

        // recording result file inspect screen
        composable(
            route = RECORDING_RESULT_FILE_INSPECT_SCREEN_NAME,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
        ) {
            RecordingResultFileInspectScreen(
                topPadding = topPadding,
                model = model
            )
        }

        // recording results comparison screen
        composable(
            route = RECORDING_RESULTS_COMPARISON_SCREEN_NAME,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
        ) {
            RecordingResultsComparisonScreen(
                topPadding = topPadding,
                model = model
            )
        }

        // settings screen
        composable(
            route = SETTINGS_SCREEN_NAME,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(250)
                )
            },
        ) {
            SettingsScreen(
                topPadding = topPadding,
                model = model
            )
        }
    }
}

/*
 * Top bar, with button that opens the navigation drawer
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    scope: CoroutineScope,
    drawerState: DrawerState,
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.top_bar_title),
                fontWeight = FontWeight.Bold,
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    drawerState.open()
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    contentDescription = null
                )
            }
        },
        actions = {
            Icon( // top right corner, use the icon of the app
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                contentDescription = null
            )
        }
    )
}

@Composable
fun DrawerContent(
    navController: NavHostController,
    scope: CoroutineScope,
    drawerState: DrawerState,
) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentScreen = currentBackStack?.destination?.route ?: HOME_SCREEN_NAME

    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(16.dp))
        navigationItems.forEachIndexed{ _, item ->
            NavigationDrawerItem(
                label = {
                    Text(text = item.title)
                },
                selected = item.title == currentScreen,
                onClick = {
                    scope.launch {
                        drawerState.close()
                    }

                    if (currentScreen != item.title)
                        navController.navigate(item.title)
                },
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = item.iconId),
                        contentDescription = item.title
                    )
                },
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}
