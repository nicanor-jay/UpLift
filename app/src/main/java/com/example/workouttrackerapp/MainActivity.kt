package com.example.workouttrackerapp

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.SportsGymnastics
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.SportsGymnastics
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.workouttrackerapp.di.AppModule
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_routine.AddEditRoutineScreen
import com.example.workouttrackerapp.feature_workout.presentation.profile.ProfileScreen
import com.example.workouttrackerapp.feature_workout.presentation.progression.ProgressionScreen
import com.example.workouttrackerapp.feature_workout.presentation.routine.RoutinesScreen
import com.example.workouttrackerapp.feature_workout.presentation.settings.SettingsScreen
import com.example.workouttrackerapp.feature_workout.presentation.widget.UpLiftWidgetReceiver
import com.example.workouttrackerapp.feature_workout.presentation.workouts.WorkoutsScreen
import com.example.workouttrackerapp.ui.theme.WorkoutTrackerAppTheme
import com.plcoding.cleanarchitectureworkoutapp.feature_workout.presentation.add_edit_workout.AddEditWorkoutScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var dataStoreManager: AppModule.DataStoreManager

    private val coroutineScope = MainScope()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val dynamicColorEnabled by dataStoreManager.dynamicColors.collectAsState(false)

            WorkoutTrackerAppTheme(dynamicColor = dynamicColorEnabled) {
                val navController = rememberNavController()
                val items = listOf(
                    BottomNavigationItem(
                        title = stringResource(R.string.workouts),
                        selectedIcon = Icons.Default.FitnessCenter,
                        unselectedIcon = Icons.Default.FitnessCenter,
                        ProfileScreen
                    ),
                    BottomNavigationItem(
                        title = stringResource(R.string.routines),
                        selectedIcon = Icons.Default.SportsGymnastics,
                        unselectedIcon = Icons.Outlined.SportsGymnastics,
                        RoutineScreen
                    ),
                    BottomNavigationItem(
                        title = stringResource(R.string.progression),
                        selectedIcon = Icons.Default.BarChart,
                        unselectedIcon = Icons.Outlined.BarChart,
                        ProgressionScreen
                    ),
                )

                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(
                            TopAppBarDefaults.windowInsets
                        ),
                        bottomBar = {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination

                            val showBottomBar = currentDestination?.hierarchy?.any {
                                it.hasRoute(ProfileScreen::class) || it.hasRoute(RoutineScreen::class) || it.hasRoute(
                                    ProgressionScreen::class
                                )
                            } == true
                            val density = LocalDensity.current


                            AnimatedVisibility(
                                showBottomBar,
                                enter = slideInVertically {
                                    // Slide in from 40 dp from the top.
                                    with(density) { 56.dp.roundToPx() }
                                },
                                exit = slideOutVertically {
                                    with(density) { 56.dp.roundToPx() }
                                } + fadeOut()
                            ) {
                                NavigationBar() {
                                    items.forEach { item ->
                                        val selected = currentDestination?.hierarchy?.any {
                                            it.hasRoute(item.route::class)
                                        } == true
                                        NavigationBarItem(
                                            selected = selected,
                                            onClick = {
                                                navController.navigate(item.route) {
                                                    popUpTo(navController.graph.findStartDestination().id) {
                                                        saveState = true
                                                    }
                                                    launchSingleTop = true
                                                    restoreState = true
                                                }
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector =
                                                    if (selected)
                                                        item.selectedIcon else
                                                        item.unselectedIcon,
                                                    contentDescription = item.title,
                                                )
                                            },
                                            label = { Text(item.title) }
                                        )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = ProfileScreen,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable<WorkoutsScreen> {
                                WorkoutsScreen(navController)
                            }
                            composable<AddEditWorkout> {
                                val args = it.toRoute<AddEditWorkout>()
                                val id = args.workoutId
                                val color = args.workoutColor

                                AddEditWorkoutScreen(
                                    navController,
                                    id,
                                    color
                                )
                            }
                            composable<RoutineScreen> {
                                RoutinesScreen(
                                    navController
                                )
                            }
                            composable<AddEditRoutine> {
                                AddEditRoutineScreen(
                                    navController,
                                )
                            }
                            composable<ProgressionScreen> {
                                ProgressionScreen(
                                    navController
                                )
                            }
                            composable<ProfileScreen> {
                                ProfileScreen(
                                    navController
                                )
                            }
                            composable<SettingsScreen> {
                                SettingsScreen(
                                    navController
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause triggered")
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                val appWidgetManager = AppWidgetManager.getInstance(applicationContext)
                val widgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(
                        applicationContext,
                        UpLiftWidgetReceiver::class.java
                    )
                )

                val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
                }

                applicationContext.sendBroadcast(intent)
            }
        }
    }
}


data class BottomNavigationItem<T>(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: T
)

@Serializable
object WorkoutsScreen

@Serializable
data class AddEditWorkout(
    val workoutId: Int = -1,
    val workoutColor: Int = -1,
    val exerciseId: Int = -1
)

@Serializable
object RoutineScreen

@Serializable
data class AddEditRoutine(
    val routineId: Int = -1,
)

@Serializable
object ProgressionScreen

@Serializable
object ProfileScreen

@Serializable
object SettingsScreen