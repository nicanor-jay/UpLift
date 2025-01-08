package com.example.workouttrackerapp.feature_workout.presentation.add_edit_routine

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.workouttrackerapp.AddEditRoutine
import com.example.workouttrackerapp.feature_workout.domain.model.Exercise
import com.example.workouttrackerapp.feature_workout.domain.model.Routine
import com.example.workouttrackerapp.feature_workout.domain.model.RoutineExercise
import com.example.workouttrackerapp.feature_workout.domain.use_case.ExerciseUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.RoutineExerciseUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.RoutineUseCases
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.WorkoutTextFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddEditRoutineViewModel @Inject constructor(
    private val exerciseUseCases: ExerciseUseCases,
    private val routineUseCases: RoutineUseCases,
    private val routineExerciseUseCases: RoutineExerciseUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _routineId = mutableStateOf<Int>(-1)
    val routineId: State<Int> = _routineId

    private val _titleState = mutableStateOf<String>("New Routine")
    val titleState: State<String> = _titleState

    private val _routineExerciseList = mutableStateOf<List<RoutineExercise>>(emptyList())
    val routineExerciseList: State<List<RoutineExercise>> = _routineExerciseList

    private val _exerciseList = mutableStateOf<List<Exercise>>(emptyList())
    val exerciseList: State<List<Exercise>> = _exerciseList

    private val _queriedExerciseList = mutableStateOf<List<Exercise>>(emptyList())
    val queriedExerciseList: State<List<Exercise>> = _queriedExerciseList

    private val _searchQuery = mutableStateOf(
        WorkoutTextFieldState(
            hint = "Search for exercise..."
        )
    )
    val searchQuery: State<WorkoutTextFieldState> = _searchQuery

    private val _isAddExerciseModalVisible = mutableStateOf(false)
    val isAddExerciseModalVisible: State<Boolean> = _isAddExerciseModalVisible

    private val _isBackHandlerDeleteConfirmationModalVisible = mutableStateOf(false)
    val isBackHandlerDeleteConfirmationModalVisible: State<Boolean> =
        _isBackHandlerDeleteConfirmationModalVisible

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            exerciseUseCases.getExercises().collect { exercisesList ->
                _exerciseList.value = exercisesList
                _queriedExerciseList.value = exercisesList
            }
        }

        savedStateHandle.toRoute<AddEditRoutine>().routineId.let { routineId ->
            Log.d("AddEditRoutineViewModel", "RoutineID: $routineId")
            _routineId.value = routineId
            if (routineId != -1) {
                viewModelScope.launch {
                    routineExerciseUseCases.getRoutineExercisesFlow(routineId)
                        .collect { routineExercises ->
                            _routineExerciseList.value = routineExercises
                        }
                }
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        _titleState.value =
                            routineUseCases.getRoutine(routineId)?.name ?: "New routine"
                    }
                }
            }
        }
    }

    private fun toggleAddExerciseModal() {
        _isAddExerciseModalVisible.value = !isAddExerciseModalVisible.value
        _searchQuery.value = searchQuery.value.copy(
            text = ""
        )
        _queriedExerciseList.value = _exerciseList.value
    }

    fun validateBackPressState(): Boolean {
        if (_routineId.value == -1 && _routineExerciseList.value.isNotEmpty()) {
            return true
        } else if (_routineId.value == 1 && _routineExerciseList.value.isEmpty()) {
            return true
        }
        return false
    }

    fun onEvent(event: AddEditRoutineEvent) {
        when (event) {
            is AddEditRoutineEvent.ToggleSelectExerciseModal -> toggleAddExerciseModal()

            is AddEditRoutineEvent.EnteredSearch -> {
                _searchQuery.value = searchQuery.value.copy(
                    text = event.value
                )
                _queriedExerciseList.value = _exerciseList.value.filter { item ->
                    item.name.contains(event.value, ignoreCase = true)
                }
            }

            is AddEditRoutineEvent.AddExerciseToRoutine -> {
                viewModelScope.launch {
                    if (_routineId.value == -1) {
                        _routineExerciseList.value += RoutineExercise(
                            null,
                            null,
                            exerciseId = event.exerciseId
                        )
                    } else {
                        routineExerciseUseCases.addRoutineExercise(
                            RoutineExercise(
                                null,
                                _routineId.value,
                                event.exerciseId
                            )
                        )
                    }
                }
                toggleAddExerciseModal()
            }

            is AddEditRoutineEvent.EnteredRoutineTitle -> {
                _titleState.value = event.value
            }

            is AddEditRoutineEvent.RemoveRoutineExercise -> {
                if (_routineId.value == -1) {
                    val index = event.value
                    val updatedList = _routineExerciseList.value.toMutableList()
                    if (index in updatedList.indices) {
                        updatedList.removeAt(index)
                        _routineExerciseList.value = updatedList
                    }
                } else {
                    val routineId = event.value

                    viewModelScope.launch {
                        routineExerciseUseCases.deleteRoutineExercise(
                            RoutineExercise(routineId, null, -1)
                        )
                    }
                }
            }

            is AddEditRoutineEvent.SaveRoutine -> {
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        _eventFlow.emit(UiEvent.SaveRoutine("${_titleState.value} saved"))
                        val routineName =
                            if (_titleState.value == "") "New routine" else _titleState.value

                        if (_routineId.value == -1) {
                            // New routine
                            val insertedId = routineUseCases.addRoutine(
                                Routine(
                                    name = routineName
                                )
                            )
                            _routineExerciseList.value.forEach { routineExercise ->
                                routineExerciseUseCases.addRoutineExercise(
                                    RoutineExercise(
                                        routineId = insertedId.toInt(),
                                        exerciseId = routineExercise.exerciseId
                                    )
                                )
                            }
                        } else {
                            // Existing routine
                            routineUseCases.updateRoutine(
                                Routine(
                                    id = _routineId.value,
                                    name = routineName
                                )
                            )
                        }
                    }
                }
            }

            AddEditRoutineEvent.ToggleBackHandlerWorkoutDeleteModal -> {
                _isBackHandlerDeleteConfirmationModalVisible.value =
                    !_isBackHandlerDeleteConfirmationModalVisible.value
            }

            AddEditRoutineEvent.DeleteRoutine -> {
                if (_routineId.value != -1) {
                    viewModelScope.launch(Dispatchers.IO) {
                        routineUseCases.deleteRoutine(
                            Routine(
                                id = _routineId.value,
                                name = ""
                            )
                        )
                    }
                }
            }
        }
    }

    sealed class UiEvent {
        data class SaveRoutine(val message: String) : UiEvent()
    }
}