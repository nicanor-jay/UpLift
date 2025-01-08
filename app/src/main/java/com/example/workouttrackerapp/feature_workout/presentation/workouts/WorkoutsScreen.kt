package com.example.workouttrackerapp.feature_workout.presentation.workouts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.workouttrackerapp.AddEditWorkout
import com.example.workouttrackerapp.BuildConfig
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.presentation.core.DeleteItemModal
import com.example.workouttrackerapp.feature_workout.presentation.workouts.components.WorkoutItem
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsScreen(
    navController: NavController,
    viewModel: WorkoutsViewModel = hiltViewModel()
) {

    val state = viewModel.state.value
    val snackbarState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val deleteWorkoutModalState = viewModel.isDeleteWorkoutModalVisible.value

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is WorkoutsViewModel.UiEvent.ShowSnackbar -> {
                    snackbarState.showSnackbar(
                        event.message
                    )
                }

                is WorkoutsViewModel.UiEvent.InsertedNewWorkout -> navController.navigate(
                    AddEditWorkout(event.insertedId)
                )
            }
        }

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.workout_history),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )

        },
        snackbarHost = {
            SnackbarHost(snackbarState) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    shape = RoundedCornerShape(20.dp),
                    snackbarData = data
                )

            }
        },
//        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.exclude(NavigationBarDefaults.windowInsets),
        floatingActionButton = {
            AnimatedVisibility(
                visible = listState.isScrollingUp(),
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(durationMillis = 300)
                )
            ) {
                FloatingActionButton(
                    onClick = {
                        viewModel.onEvent(WorkoutsEvent.AddWorkout)
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,

                    ) {
                    Icon(Icons.Default.Add, "Add Workout")
                }
            }
        },
    ) { padding ->
        if (state.workouts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(R.string.no_existing_workouts_press_to_add),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                    )
                    if (BuildConfig.DEBUG) {
                        Button(onClick = {
                            viewModel.generateWorkouts()
                        }) { "Generate data" }
                    }
                }
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                items(state.workouts, key = { item -> item.workoutId }) { workout ->
                    WorkoutItem(
                        workout,
                        modifier = Modifier
                            .clickable {
                                navController.navigate(
                                    AddEditWorkout(
                                        workout.workoutId,
                                        workoutColor = workout.color
                                    )
                                )
                            }
                            .fillMaxWidth()
                            .padding(5.dp)
                            .animateItem(),
                        onDeleteClick = {
                            viewModel.onEvent(
                                WorkoutsEvent.ToggleDeleteWorkoutModal(workout.workoutId)
                            )
                        }
                    )
                }
            }
        }
    }

    if (deleteWorkoutModalState) {
        DeleteItemModal(
            modalHeader = stringResource(R.string.delete_workout),
            modalDescription = stringResource(R.string.you_re_about_to_delete_this_workout_are_you_sure),
            onConfirm = { viewModel.onEvent(WorkoutsEvent.DeleteWorkout) },
            onCancel = { viewModel.onEvent(WorkoutsEvent.ToggleDeleteWorkoutModal(null)) },
            onDismiss = { viewModel.onEvent(WorkoutsEvent.ToggleDeleteWorkoutModal(null)) }
        )

    }
}

@Composable
private fun LazyListState.isScrollingUp(): Boolean {
    var previousIndex by remember(this) { mutableIntStateOf(firstVisibleItemIndex) }
    var previousScrollOffset by remember(this) { mutableIntStateOf(firstVisibleItemScrollOffset) }
    return remember(this) {
        derivedStateOf {
            if (previousIndex != firstVisibleItemIndex) {
                previousIndex > firstVisibleItemIndex
            } else {
                previousScrollOffset >= firstVisibleItemScrollOffset
            }.also {
                previousIndex = firstVisibleItemIndex
                previousScrollOffset = firstVisibleItemScrollOffset
            }
        }
    }.value
}

