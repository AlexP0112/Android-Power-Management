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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.powermanager.R
import com.example.powermanager.ui.model.AppModel
import com.example.powermanager.ui.navigation.navigationItems
import com.example.powermanager.ui.screens.ControlScreen
import com.example.powermanager.ui.screens.HomeScreen
import com.example.powermanager.ui.screens.StatisticsScreen
import com.example.powermanager.utils.CONTROL_SCREEN_NAME
import com.example.powermanager.utils.HOME_SCREEN_NAME
import com.example.powermanager.utils.STATISTICS_SCREEN_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PowerManagerApp(
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    model: AppModel,
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
                    model = model,
                )
            },
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        scope = scope,
                        drawerState = drawerState
                    )
                }
            ) {
                ScreensNavHost(
                    navController = navController,
                    topPadding = it.calculateTopPadding(),
                    model = model,
                    onBack = {
                        val lastScreenName = navController.previousBackStackEntry?.destination?.route
                        if (lastScreenName != null && lastScreenName != model.uiState.value.currentScreenName) {
                            navController.navigate(lastScreenName)
                            model.changeAppScreen(lastScreenName)
                        }
                    }
                )
            }
        }
    }
}

/*
 * Navigation host that manages navigation between app screens (Home, Statistics, Control)
 */
@Composable
fun ScreensNavHost(
    navController: NavHostController,
    topPadding: Dp,
    model: AppModel,
    onBack : () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = HOME_SCREEN_NAME
    ) {
        composable(
            route = HOME_SCREEN_NAME,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(300)
                )
            },
        ) {
            HomeScreen(
                topPadding = topPadding,
                onBack = onBack
            )
        }

        composable(
            route = STATISTICS_SCREEN_NAME,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(300)
                )
            },
        ) {
            StatisticsScreen(
                topPadding = topPadding,
                model = model,
                onBack = onBack
            )
        }

        composable(
            route = CONTROL_SCREEN_NAME,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Companion.Left,
                    animationSpec = tween(300)
                )
            },
        ) {
            ControlScreen(
                topPadding = topPadding,
                onBack = onBack
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(R.string.top_bar_title)
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
                    contentDescription = null
                )
            }
        },
        actions = {
            Icon( // top right corner, use the icon of the app
                painter = painterResource(id = R.mipmap.ic_launcher_foreground),
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
    model: AppModel,
) {
    ModalDrawerSheet {
        Spacer(modifier = Modifier.height(16.dp))
        navigationItems.forEachIndexed{ _, item ->
            NavigationDrawerItem(
                label = {
                    Text(text = item.title)
                },
                selected = false,
                onClick = {
                    scope.launch {
                        drawerState.close()
                    }

                    if (item.title != model.uiState.value.currentScreenName) {
                        navController.navigate(item.title)
                        model.changeAppScreen(item.title)
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                modifier = Modifier
                    .padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}
