package com.example.powermanager.ui.main

import android.annotation.SuppressLint
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.powermanager.R
import com.example.powermanager.ui.navigation.CONTROL_SCREEN_NAME
import com.example.powermanager.ui.navigation.HOME_SCREEN_NAME
import com.example.powermanager.ui.navigation.STATISTICS_SCREEN_NAME
import com.example.powermanager.ui.navigation.navigationItems
import com.example.powermanager.ui.screens.ControlScreen
import com.example.powermanager.ui.screens.HomeScreen
import com.example.powermanager.ui.screens.StatisticsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PowerManagerApp(
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
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
                    drawerState = drawerState
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
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun ScreensNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HOME_SCREEN_NAME
    ) {
        composable(route = HOME_SCREEN_NAME) {
            HomeScreen()
        }

        composable(route = STATISTICS_SCREEN_NAME) {
            StatisticsScreen()
        }

        composable(route = CONTROL_SCREEN_NAME) {
            ControlScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(scope: CoroutineScope, drawerState: DrawerState) {
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
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerContent(
    navController: NavHostController,
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    ModalDrawerSheet {
        var selectedNavigationItemIndex by rememberSaveable {
            mutableIntStateOf(0)
        }

        Spacer(modifier = Modifier.height(16.dp))
        navigationItems.forEachIndexed{ index, item ->
            NavigationDrawerItem(
                label = {
                    Text(text = item.title)
                },
                selected = index == selectedNavigationItemIndex,
                onClick = {
                    selectedNavigationItemIndex = index
                    navController.navigate(item.title)
                    scope.launch {
                        drawerState.close()
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
