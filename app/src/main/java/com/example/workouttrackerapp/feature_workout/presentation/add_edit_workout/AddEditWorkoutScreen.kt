package com.plcoding.cleanarchitectureworkoutapp.feature_workout.presentation.add_edit_workout

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.domain.model.Workout
import com.example.workouttrackerapp.feature_workout.domain.util.PastOrPresentSelectableDates
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.AddEditWorkoutEvent
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.AddEditWorkoutViewModel
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components.EditNoteModal
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components.EditRecordModal
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components.ExerciseOptionsModal
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components.ImportRoutineModal
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components.SelectExerciseModal
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components.WorkoutExerciseItem
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components.WorkoutOptionsModal
import com.example.workouttrackerapp.feature_workout.presentation.core.DeleteItemModal
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note.components.TransparentHintTextField
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditWorkoutScreen(
    navController: NavController,
    workoutId: Int,
    workoutColor: Int,
    viewModel: AddEditWorkoutViewModel = hiltViewModel()
) {
    val contentState = viewModel.workoutNote.value
    val queryState = viewModel.searchQuery.value
    val exercisesList = viewModel.exerciseList.value
    val queriedExercisesList = viewModel.queriedExerciseList.value

    val recordState = viewModel.recordsList.value
    val workoutExerciseState = viewModel.workoutExerciseList.value
    val isExpandedMap = viewModel.expandedStates

    val prevRecordState = viewModel.exerciseRecordsMap

    val timestampState = viewModel.workoutTimestamp.value
    val datePickerModalState = viewModel.isDatePickerVisible.value
    val addExerciseModalState = viewModel.isAddExerciseModalVisible.value
    val changeExerciseModalState = viewModel.isChangeExerciseModalVisible.value

    val importRoutineModalState = viewModel.isImportRoutineModalVisible.value
    val routinesList = viewModel.routinesListState.value

    val overviewOptionsModalState = viewModel.isOverviewOptionsModalVisible.value
    val deleteConfirmationModalState = viewModel.isDeleteConfirmationModalVisible.value

    val exerciseOptionsModalState = viewModel.isExerciseOptionsModalVisible.value
    val currentWorkoutExerciseState = viewModel.currentWorkoutExercise.value

    val backHandlerDeleteModalState = viewModel.isBackHandlerDeleteConfirmationModalVisible.value

    val editRecordModalState = viewModel.isEditRecordModalVisible.value
    val currentRecordState = viewModel.currentRecord.value
    val currentRecordDetailsState = viewModel.currentRecordDetails.value

    val exerciseNoteModalState = viewModel.isExerciseNoteModalVisible.value

    val workoutBackgroundAnimatable = remember {
        Animatable(
            if (workoutColor != -1) Workout.workoutColors[workoutColor]
            else Workout.workoutColors[viewModel.workoutColor.value]
        )
    }

    val weightLabel = viewModel.weightUnitPref.value

    val snackbarState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.UK)

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()


    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AddEditWorkoutViewModel.UiEvent.ShowSnackbar -> {
                    snackbarState.showSnackbar(
                        message = event.message
                    )
                }

                is AddEditWorkoutViewModel.UiEvent.SaveWorkout -> {
                    navController.navigateUp()
                }
            }
        }
    }

    fun onBackPressed() {
        if (viewModel.workoutExerciseList.value.isEmpty()) {
            viewModel.onEvent(AddEditWorkoutEvent.ToggleBackHandlerWorkoutDeleteModal)
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
                title = {
                    Text(
                        stringResource(R.string.edit_workout),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackPressed()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(AddEditWorkoutEvent.SaveWorkout)
            }) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Workout.workoutColors.forEachIndexed { index, color ->
                    Box(modifier = Modifier
                        .size(50.dp)
                        .shadow(15.dp, CircleShape)
                        .clip(CircleShape)
                        .background(color)
                        .border(
                            width = 3.dp, color = if (viewModel.workoutColor.value == index) {
                                Color.Black
                            } else Color.Transparent, shape = CircleShape
                        )
                        .clickable {
                            scope.launch {
                                workoutBackgroundAnimatable.animateTo(
                                    targetValue = Workout.workoutColors[index],
                                    animationSpec = tween(
                                        durationMillis = 500
                                    )
                                )
                            }
                            viewModel.onEvent(AddEditWorkoutEvent.ChangeColor(index))
                        })
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small))
            ) {

                Box(contentAlignment = Alignment.TopStart) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        "Bookmark",
                        tint = workoutBackgroundAnimatable.value,
                        modifier = Modifier
                            .scale(1.5f)
                            .padding(start = dimensionResource(R.dimen.padding_medium))
                    )
                }
                Column(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_medium))) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                stringResource(R.string.overview),
                                style = MaterialTheme.typography.titleLarge
                            )

                            IconButton({ viewModel.onEvent(AddEditWorkoutEvent.ToggleOverviewOptionsModal) }) {
                                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)) {
                                    Icon(
                                        Icons.Default.MoreHoriz,
                                        "Exercise Options",
                                        modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_extra_small))
                                    )
                                }
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                formatter.format(timestampState),
                                style = MaterialTheme.typography.titleMedium
                            )
                            IconButton(onClick = { viewModel.onEvent(AddEditWorkoutEvent.ToggleDatePicker) }) {
                                Icon(Icons.Default.Edit, "Edit date")
                            }
                        }
                    }
                    Text(
                        stringResource(R.string.note),
                        style = MaterialTheme.typography.titleMedium
                    )
                    TransparentHintTextField(
                        text = contentState.text,
                        hint = contentState.hint,
                        onValueChange = {
                            viewModel.onEvent(AddEditWorkoutEvent.EnteredNote(it))
                        },
                        onFocusChange = {
                            viewModel.onEvent(AddEditWorkoutEvent.ChangeNoteFocus(it))
                        },
                        isHintVisible = contentState.isHintVisible,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onPrimaryContainer),
                        modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_medium))
                    )
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.padding_small))
            ) {
                Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
                    Text(
                        stringResource(R.string.exercises),
                        style = MaterialTheme.typography.titleLarge
                    )
                    if (workoutExerciseState.isNotEmpty() && recordState.isNotEmpty()) {
                        for (workoutExercise in workoutExerciseState) {
                            val recordList = recordState.filter { record ->
                                record.workoutExerciseId == workoutExercise.id
                            }
                            val lastRecord =
                                if (recordList.isNotEmpty()) recordList.last() else null
                            WorkoutExerciseItem(
                                workoutExercise = workoutExercise,
                                exercise = exercisesList[workoutExercise.exerciseId - 1],
                                toggleExpand = {
                                    viewModel.onEvent(
                                        AddEditWorkoutEvent.ToggleCardExpand(it)
                                    )
                                },
                                expandedState = isExpandedMap[workoutExercise.id] ?: false,
                                recordList = recordList,
                                prevRecordsList = prevRecordState[workoutExercise.exerciseId]
                                    ?: emptyList(),
                                addSet = {
                                    viewModel.onEvent(
                                        AddEditWorkoutEvent.AddSetToExercise(
                                            it, lastRecord
                                        )
                                    )
                                },
                                toggleEditSetModal = {
                                    viewModel.onEvent(AddEditWorkoutEvent.ToggleEditSetModal(it))
                                },
                                toggleExerciseOptionsModal = {
                                    viewModel.onEvent(
                                        AddEditWorkoutEvent.ToggleExerciseOptionsModal(it)
                                    )
                                },
                                weightLabel = weightLabel
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = dimensionResource(R.dimen.padding_small)),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = {
                            viewModel.onEvent(AddEditWorkoutEvent.ToggleAddExerciseModal)
                        }) {
                            Icon(Icons.Default.Add, "Add Exercise")
                            Text(stringResource(R.string.add_exercise))
                        }

                        if (workoutExerciseState.isEmpty() && routinesList.isNotEmpty()) {
                            Text(stringResource(R.string.or))
                            Button(onClick = {
                                viewModel.onEvent(AddEditWorkoutEvent.ToggleImportRoutineModal)
                            }) {
                                Icon(Icons.Default.Add, stringResource(R.string.import_routine))
                                Text(stringResource(R.string.import_routine))
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(2 * FloatingActionButtonDefaults.LargeIconSize))

        }
    }

    if (datePickerModalState) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = timestampState,
            selectableDates = PastOrPresentSelectableDates
        )

        DatePickerDialog(onDismissRequest = { viewModel.onEvent(AddEditWorkoutEvent.ToggleDatePicker) },
            dismissButton = {
                Button(onClick = { viewModel.onEvent(AddEditWorkoutEvent.ToggleDatePicker) }) {
                    Text(
                        "Cancel"
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    (datePickerState.selectedDateMillis ?: timestampState)?.let {
                        AddEditWorkoutEvent.EnteredTimestamp(
                            it
                        )
                    }?.let {
                        viewModel.onEvent(
                            it
                        )
                    }
                    viewModel.onEvent(AddEditWorkoutEvent.ToggleDatePicker)
                }) { Text(stringResource(R.string.set)) }
            }) {
            DatePicker(state = datePickerState)
        }
    }

    if (addExerciseModalState) {
        SelectExerciseModal(
            exerciseList = queriedExercisesList,
            onClick = { viewModel.onEvent(AddEditWorkoutEvent.AddExerciseToWorkout(it)) },
            onDismissRequest = { viewModel.onEvent(AddEditWorkoutEvent.ToggleAddExerciseModal) },
            queryState = queryState,
            onQueryChange = { viewModel.onEvent(AddEditWorkoutEvent.EnteredSearch(it)) })
    }

    if (changeExerciseModalState) {
        SelectExerciseModal(
            exerciseList = queriedExercisesList,
            onClick = { viewModel.onEvent(AddEditWorkoutEvent.ChangeExercise(it)) },
            onDismissRequest = { viewModel.onEvent(AddEditWorkoutEvent.ToggleChangeExerciseModal) },
            queryState = queryState,
            onQueryChange = { viewModel.onEvent(AddEditWorkoutEvent.EnteredSearch(it)) })
    }

    if (importRoutineModalState) {
        ImportRoutineModal(
            routineList = routinesList,
            onClick = { viewModel.onEvent(AddEditWorkoutEvent.AddRoutineExercisesToWorkout(it)) },
            onDismissRequest = { viewModel.onEvent(AddEditWorkoutEvent.ToggleImportRoutineModal) })
    }

    if (exerciseOptionsModalState && currentWorkoutExerciseState != null) {
        ExerciseOptionsModal(modifier = Modifier,
            onDismissRequest = {
                viewModel.onEvent(
                    AddEditWorkoutEvent.ToggleExerciseOptionsModal(
                        null
                    )
                )
            },
            exercise = exercisesList.find { it.id == currentWorkoutExerciseState.exerciseId }!!,
            workoutExercise = currentWorkoutExerciseState,
            toggleEditNoteModal = {
                viewModel.onEvent(AddEditWorkoutEvent.ToggleEditNoteModal)
            },
            toggleChangeExerciseModal = {
                viewModel.onEvent(AddEditWorkoutEvent.ToggleChangeExerciseModal)
            },
            deleteWorkoutExercise = {
                viewModel.onEvent(
                    AddEditWorkoutEvent.DeleteWorkoutExercise(
                        it
                    )
                )
            })
    }

    if (editRecordModalState && currentRecordState != null && currentRecordDetailsState != null) {
        EditRecordModal(modifier = Modifier,
            record = currentRecordState,
            onDismissRequest = { viewModel.onEvent(AddEditWorkoutEvent.ToggleEditSetModal(null)) },
            recordDetails = currentRecordDetailsState,
            updateUiState = {
                viewModel.onEvent(AddEditWorkoutEvent.UpdateRecordDetails(it))
            },
            saveRecord = {
                viewModel.onEvent(AddEditWorkoutEvent.UpdateRecord)
            },
            deleteRecord = {
                viewModel.onEvent(AddEditWorkoutEvent.DeleteRecord)
            },
            deleteEnabled = workoutExerciseState.isNotEmpty() &&
                    recordState.isNotEmpty() &&
                    (workoutExerciseState.find { it.id == currentRecordState.workoutExerciseId }
                        ?.let {
                            recordState.count { record -> record.workoutExerciseId == it.id }
                        } ?: 0) > 1)
    }

    if (exerciseNoteModalState && currentWorkoutExerciseState != null) {
        EditNoteModal(modifier = Modifier, note = currentWorkoutExerciseState.note, saveNote = {
            viewModel.onEvent(AddEditWorkoutEvent.SaveExerciseNote(it))
        }, onDismissRequest = { viewModel.onEvent(AddEditWorkoutEvent.ToggleEditNoteModal) })
    }

    if (overviewOptionsModalState) {
        WorkoutOptionsModal(
            onDeleteClick = { viewModel.onEvent(AddEditWorkoutEvent.ToggleDeleteConfirmationModal) },
            onDismissRequest = { viewModel.onEvent(AddEditWorkoutEvent.ToggleOverviewOptionsModal) })
    }
    if (deleteConfirmationModalState) {
        DeleteItemModal(
            modalHeader = "Delete Workout",
            modalDescription = "You're about to delete this workout.\n Are you sure?",
            onConfirm = {
                navController.navigateUp()
                viewModel.onEvent(AddEditWorkoutEvent.DeleteWorkout)
            },
            onCancel = { viewModel.onEvent(AddEditWorkoutEvent.ToggleDeleteConfirmationModal) },
            onDismiss = { viewModel.onEvent(AddEditWorkoutEvent.ToggleDeleteConfirmationModal) }
        )
    }
    if (backHandlerDeleteModalState) {
        DeleteItemModal(
            modalHeader = "Delete Empty Workout?",
            modalDescription = "This workout is empty. Do you want to delete this empty workout?",
            onConfirm = {
                viewModel.onEvent(AddEditWorkoutEvent.ToggleBackHandlerWorkoutDeleteModal)
                navController.navigateUp()
                viewModel.onEvent(AddEditWorkoutEvent.DeleteWorkout)
            },
            onCancel = {
                viewModel.onEvent(AddEditWorkoutEvent.ToggleBackHandlerWorkoutDeleteModal)
                navController.navigateUp()
            },
            onDismiss = {
                viewModel.onEvent(AddEditWorkoutEvent.ToggleBackHandlerWorkoutDeleteModal)
            }
        )
    }
}

