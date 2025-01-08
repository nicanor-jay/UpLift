package com.example.workouttrackerapp.feature_workout.presentation.routine

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttrackerapp.feature_workout.domain.model.Routine
import com.example.workouttrackerapp.feature_workout.domain.model.helper.RoutineWithExerciseCount
import com.example.workouttrackerapp.feature_workout.domain.use_case.ExerciseUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.RoutineUseCases
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.AddEditWorkoutViewModel.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class RoutinesViewModel @Inject constructor(
    private val exerciseUseCases: ExerciseUseCases,
    private val routinesUseCases: RoutineUseCases
) : ViewModel() {

    private val _routinesListState = mutableStateOf<List<RoutineWithExerciseCount>>(emptyList())
    val routinesListState: State<List<RoutineWithExerciseCount>> = _routinesListState

    private val _insertedRoutineId = mutableStateOf<Int>(-1)
    val insertedRoutineId: State<Int> = _insertedRoutineId

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _isDeleteRoutineModalVisible = mutableStateOf(false)
    val isDeleteRoutineModalVisible: State<Boolean> = _isDeleteRoutineModalVisible

    private val _routineIdToBeDeleted = mutableStateOf<Int?>(null)

    init {
        viewModelScope.launch {
            routinesUseCases.getRoutinesWithExerciseCount().collect { routineList ->
                _routinesListState.value = routineList
            }
        }
    }

    private fun toggleDeleteRoutineModal() {
        _isDeleteRoutineModalVisible.value = !_isDeleteRoutineModalVisible.value
    }

    fun onEvent(event: RoutineEvent) {
        when (event) {
            is RoutineEvent.InsertRoutine ->
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        _insertedRoutineId.value = routinesUseCases.addRoutine(
                            Routine(null, "New Routine")
                        ).toInt()
                    }
                    _eventFlow.emit(UiEvent.NavigateToNewRoutine)
                }

            is RoutineEvent.DeleteRoutine ->
                viewModelScope.launch {
                    _eventFlow.emit(
                        UiEvent.ShowSnackbar(
                            "Deleted ${
                                routinesListState.value.find {
                                    it.routineId ==
                                            _routineIdToBeDeleted.value!!
                                }?.routineName ?: "routine"
                            }"
                        )
                    )
                    withContext(Dispatchers.IO) {
                        routinesUseCases.deleteRoutine(
                            Routine(
                                id = _routineIdToBeDeleted.value,
                                ""
                            )
                        )
                    }
                    toggleDeleteRoutineModal()
                }

            is RoutineEvent.ToggleDeleteRoutineModal -> {
                Log.d("RoutinesViewModel", "Routine ID to be deleted : ${event.routineId}")
                if (!_isDeleteRoutineModalVisible.value) {
                    // Set routineId to be deleted
                    _routineIdToBeDeleted.value = event.routineId
                } else {
                    // Clear routineId
                    _routineIdToBeDeleted.value = null
                }
                // Toggle the modal bool
                toggleDeleteRoutineModal()
            }
        }
    }

    sealed class UiEvent {
        object NavigateToNewRoutine : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }
}