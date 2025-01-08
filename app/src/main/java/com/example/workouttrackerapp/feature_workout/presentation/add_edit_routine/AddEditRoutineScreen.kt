package com.example.workouttrackerapp.feature_workout.presentation.add_edit_routine

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components.SelectExerciseModal
import com.example.workouttrackerapp.feature_workout.presentation.core.DeleteItemModal
import com.example.workouttrackerapp.feature_workout.presentation.routine.components.RoutineExerciseItem
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRoutineScreen(
    navController: NavController,
    viewModel: AddEditRoutineViewModel = hiltViewModel()
) {

    val addExerciseModalState = viewModel.isAddExerciseModalVisible.value
    val exerciseList = viewModel.exerciseList.value
    val queriedExerciseList = viewModel.queriedExerciseList.value
    val queryState = viewModel.searchQuery.value

    val routineExerciseList = viewModel.routineExerciseList.value
    val titleState = viewModel.titleState.value

    val backHandlerDeleteModalState = viewModel.isBackHandlerDeleteConfirmationModalVisible.value

    val routineId = viewModel.routineId.value

    val snackbarState = remember { SnackbarHostState() }
    val focusRequester = remember { FocusRequester() }

    val topAppBarText = if (routineId == -1) {
        "Add Routine"
    } else {
        "Edit Routine"
    }

    Log.d("AddEditRoutineScreen", "titleState : $titleState")

    val titleValue =
        remember(titleState) {
            mutableStateOf(
                TextFieldValue(
                    titleState ?: "",
                    TextRange(titleState?.length ?: 0)
                )
            )
        }

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditRoutineViewModel.UiEvent.SaveRoutine -> {
                    navController.navigateUp()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (routineId == -1) {
            focusRequester.requestFocus()
        }
    }

    fun onBackPressed() {
        if (viewModel.validateBackPressState()) {
            viewModel.onEvent(AddEditRoutineEvent.ToggleBackHandlerWorkoutDeleteModal)
        } else {
            navController.navigateUp()
        }
    }

    BackHandler {
        onBackPressed()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(topAppBarText, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackPressed()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(AddEditRoutineEvent.SaveRoutine)
                }
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save workout")
            }
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
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_medium))
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_medium))
                        .focusRequester(focusRequester),
                    value = titleValue.value,
                    onValueChange = {
                        titleValue.value = it
                        viewModel.onEvent(AddEditRoutineEvent.EnteredRoutineTitle(it.text))
                    },
                    label = { Text(stringResource(R.string.title)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )

                LazyColumn(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
                    itemsIndexed(routineExerciseList) { index, routineExercise ->
                        RoutineExerciseItem(
                            Modifier,
                            index,
                            routineExercise,
                            exerciseList[routineExercise.exerciseId - 1],
                            onClick = {
                                viewModel.onEvent(AddEditRoutineEvent.RemoveRoutineExercise(it))
                            })
                    }
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = dimensionResource(R.dimen.padding_small)),
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Button(
                                onClick = {
                                    viewModel.onEvent(AddEditRoutineEvent.ToggleSelectExerciseModal)
                                }
                            ) {
                                Icon(Icons.Default.Add, stringResource(R.string.add_exercise))
                                Text(" ")
                                Text(stringResource(R.string.add_exercise))
                            }
                        }
                    }
                }
            }
        }
    }

    if (addExerciseModalState) {
        SelectExerciseModal(
            exerciseList = queriedExerciseList,
            onClick = { viewModel.onEvent(AddEditRoutineEvent.AddExerciseToRoutine(it)) },
            queryState = queryState,
            onQueryChange = { viewModel.onEvent(AddEditRoutineEvent.EnteredSearch(it)) },
            onDismissRequest = {
                viewModel.onEvent(AddEditRoutineEvent.ToggleSelectExerciseModal)
            }
        )
    }
    if (backHandlerDeleteModalState) {
        if (routineId != -1 && viewModel.routineExerciseList.value.isEmpty()) {
            // if not new routine and routine exercise list empty
            DeleteItemModal(
                modalHeader = "Delete Empty Routine?",
                modalDescription = "This routine is empty.\nDo you want to delete this empty routine?",
                onConfirm = {
                    viewModel.onEvent(AddEditRoutineEvent.ToggleBackHandlerWorkoutDeleteModal)
                    navController.navigateUp()
                    viewModel.onEvent(AddEditRoutineEvent.DeleteRoutine)
                },
                onCancel = {
                    viewModel.onEvent(AddEditRoutineEvent.ToggleBackHandlerWorkoutDeleteModal)
                    navController.navigateUp()
                },
                onDismiss = {
                    viewModel.onEvent(AddEditRoutineEvent.ToggleBackHandlerWorkoutDeleteModal)
                }
            )
        } else if (routineId == -1 && viewModel.routineExerciseList.value.isNotEmpty()) {
            DeleteItemModal(
                modalHeader = "Discard Routine",
                modalDescription = "You haven't saved this routine.\nDo you want to discard this routine?",
                onConfirm = {
                    viewModel.onEvent(AddEditRoutineEvent.ToggleBackHandlerWorkoutDeleteModal)
                    navController.navigateUp()
                    viewModel.onEvent(AddEditRoutineEvent.DeleteRoutine)
                },
                onCancel = {
                    viewModel.onEvent(AddEditRoutineEvent.ToggleBackHandlerWorkoutDeleteModal)
                    viewModel.onEvent(AddEditRoutineEvent.SaveRoutine)
//                    navController.navigateUp()
                },
                onDismiss = {
                    viewModel.onEvent(AddEditRoutineEvent.ToggleBackHandlerWorkoutDeleteModal)
                }
            )
        }
    }
}