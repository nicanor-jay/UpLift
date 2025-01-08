package com.example.workouttrackerapp.feature_workout.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.workouttrackerapp.AddEditWorkout
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.SettingsScreen
import com.example.workouttrackerapp.WorkoutsScreen
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components.SelectExerciseModal
import com.example.workouttrackerapp.feature_workout.presentation.profile.components.WorkoutContributionCalendar
import com.example.workouttrackerapp.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {

    val workoutList = viewModel.workoutList.value
    val workoutContributionList = viewModel.workoutContributionList.value
    val currentStreak = viewModel.currentStreak.value
    val isLoading = viewModel.isLoading.value
    val exercisesModalState = viewModel.isExerciseListModalVisible.value
    val weeklyTargetModalState = viewModel.isWeeklyTargetModalVisible.value
    val currentWeeklyTarget = viewModel.currentWeeklyTarget.value
    val weekStartDay = viewModel.weekStartDay.value

    val emphasisedTextColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    LaunchedEffect(true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is ProfileViewModel.UiEvent.InsertedNewWorkout -> navController.navigate(
                    AddEditWorkout(event.insertedId)
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.app_name), color = MaterialTheme.colorScheme.primary) },
                actions = {
                    IconButton({ navController.navigate(SettingsScreen) }) {
                        Icon(
                            Icons.Default.Settings,
                            stringResource(R.string.settings)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
//                    navController.navigate(AddEditWorkout(-1))
                    viewModel.onEvent(ProfileEvent.InsertNewWorkout)
                },
            ) {
                Icon(Icons.Default.Add, stringResource(R.string.add_workout))
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_extra_small)))
                Text(stringResource(R.string.add_workout))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(dimensionResource(R.dimen.padding_extra_small))
                .verticalScroll(rememberScrollState()),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Card(
                    Modifier
                        .weight(1f)
                        .padding(dimensionResource(R.dimen.padding_small))
                        .clickable {
                            navController.navigate(
                                WorkoutsScreen
                            )
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)

//                    elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen.padding_extra_small)),
//                    colors = CardDefaults.cardColors()
                ) {
                    Column(
                        Modifier.padding(dimensionResource(R.dimen.padding_medium)),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.FitnessCenter, "Number of workouts",
                                tint = Orange,
                                modifier = Modifier.size(with(LocalDensity.current) { MaterialTheme.typography.headlineMedium.fontSize.toDp() })
                            )
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_extra_small)))
                            Text(
                                workoutList.size.toString(),
                                modifier = Modifier,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Medium,
                                color = emphasisedTextColor
                            )
                        }
                        Text(
                            "workouts",
                            modifier = Modifier,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Card(
                    Modifier
                        .weight(1f)
                        .padding(dimensionResource(R.dimen.padding_small))
                        .clickable {
                            viewModel.onEvent(ProfileEvent.ToggleWeeklyTargetModal)
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Column(
                        Modifier.padding(dimensionResource(R.dimen.padding_medium)),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.LocalFireDepartment,
                                "current streak",
                                tint = Orange,
                                modifier = Modifier.size(with(LocalDensity.current) { MaterialTheme.typography.headlineMedium.fontSize.toDp() })
                            )
                            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_extra_small)))
                            Text(
                                currentStreak.toString() + "w",
                                modifier = Modifier,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Medium,
                                color = emphasisedTextColor
                            )
                        }
                        Text(
                            "streak",
                            modifier = Modifier,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)

            ) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    WorkoutContributionCalendar(
                        contributionList = workoutContributionList,
                        navController = navController,
                        weekStartDay = weekStartDay
                    )

                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small))
                    .clickable { viewModel.onEvent(ProfileEvent.ToggleExerciseModal) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    "Exercises",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_medium))
                )
            }
        }
    }
    if (exercisesModalState && viewModel.exerciseList.value.isNotEmpty()) {
        SelectExerciseModal(
            exerciseList = viewModel.queriedExerciseList.value,
            onClick = {},
            queryState = viewModel.searchQuery.value,
            onQueryChange = { viewModel.onEvent(ProfileEvent.EnteredSearch(it)) },
            onDismissRequest = { viewModel.onEvent(ProfileEvent.ToggleExerciseModal) },
            focusSearchOnToggle = false
        )
    }

    if (weeklyTargetModalState) {
        val selectionItems = listOf(1, 2, 3, 4, 5, 6, 7)
        Dialog(onDismissRequest = { viewModel.onEvent(ProfileEvent.ToggleWeeklyTargetModal) }) {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
                ) {
                    Text(
                        "Weekly Target",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    selectionItems.forEach { item ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.onEvent(ProfileEvent.EnteredWeeklyTarget(item))
                                },
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                RadioButton(
                                    selected = currentWeeklyTarget == item,
                                    onClick = {
                                        viewModel.onEvent(
                                            ProfileEvent.EnteredWeeklyTarget(
                                                item
                                            )
                                        )
                                    }
                                )
                                Text(
                                    "${item}x per week",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(dimensionResource(R.dimen.padding_medium)),
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold

                                )
                            }
                        }
                    }
                }
            }
        }

    }
}