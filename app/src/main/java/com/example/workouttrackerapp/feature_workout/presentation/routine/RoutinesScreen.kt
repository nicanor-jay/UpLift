package com.example.workouttrackerapp.feature_workout.presentation.routine

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.workouttrackerapp.AddEditRoutine
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.presentation.core.DeleteItemModal
import com.example.workouttrackerapp.feature_workout.presentation.routine.components.RoutineItem
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(
    navController: NavController,
    viewModel: RoutinesViewModel = hiltViewModel()
) {

    val routinesList = viewModel.routinesListState.value
    val deleteRoutineModalState = viewModel.isDeleteRoutineModalVisible.value

    val snackbarState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is RoutinesViewModel.UiEvent.NavigateToNewRoutine -> {
                    navController.navigate(AddEditRoutine(viewModel.insertedRoutineId.value))
                }

                is RoutinesViewModel.UiEvent.ShowSnackbar -> {
                    snackbarState.showSnackbar(
                        event.message
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.routines), color = MaterialTheme.colorScheme.primary) },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(AddEditRoutine(-1))
                }
            ) {
                Icon(Icons.Default.Add, "Add Routine")
            }
        }
    ) { padding ->
        if (routinesList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(R.string.no_existing_routines_press_to_add),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,

                        )
                }
            }
        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
            ) {
                items(routinesList, key = { item -> item.routineId }) { routine ->
                    RoutineItem(
                        modifier = Modifier.animateItem(),
                        routine,
                        onClick = { navController.navigate(AddEditRoutine(it)) },
                        deleteRoutine = { viewModel.onEvent(RoutineEvent.ToggleDeleteRoutineModal(it)) }
                    )
                }
            }
        }

        if (deleteRoutineModalState) {
            DeleteItemModal(
                modalHeader = "Delete Routine",
                modalDescription = "You're about to delete this routine.\n Are you sure?",
                onConfirm = {
                    viewModel.onEvent(RoutineEvent.DeleteRoutine)
                },
                onCancel = { viewModel.onEvent(RoutineEvent.ToggleDeleteRoutineModal(null)) },
                onDismiss = { viewModel.onEvent(RoutineEvent.ToggleDeleteRoutineModal(null)) },
            )
        }

    }
}
