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
import com.example.powermanager.ui.screens.ControlScreen
import com.example.powermanager.ui.screens.HomeScreen
import com.example.powermanager.ui.screens.SettingsScreen
import com.example.powermanager.ui.screens.StatisticsScreen
import com.example.powermanager.utils.CONTROL_SCREEN_NAME
import com.example.powermanager.utils.HOME_SCREEN_NAME
import com.example.powermanager.utils.SETTINGS_SCREEN_NAME
import com.example.powermanager.utils.STATISTICS_SCREEN_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PowerManagerApp(
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    model: PowerManagerAppModel,
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
                )
            }
        }
    }
}

/*
 * Navigation host that manages navigation between app screens (Home, Statistics, Control, Settings)
 */
@Composable
fun ScreensNavHost(
    navController: NavHostController,
    topPadding: Dp,
    model: PowerManagerAppModel,
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
                model = model,
                navController = navController
            )
        }

        // statistics screen
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
            )
        }

        // control screen
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
            )
        }

        // settings screen
        composable(
            route = SETTINGS_SCREEN_NAME,
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
            SettingsScreen(
                topPadding = topPadding,
            )
        }
    }
}

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
