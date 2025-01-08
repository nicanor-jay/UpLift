package com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.workouttrackerapp.AddEditWorkout
import com.example.workouttrackerapp.di.AppModule
import com.example.workouttrackerapp.feature_workout.domain.model.Exercise
import com.example.workouttrackerapp.feature_workout.domain.model.Record
import com.example.workouttrackerapp.feature_workout.domain.model.Workout
import com.example.workouttrackerapp.feature_workout.domain.model.WorkoutExercise
import com.example.workouttrackerapp.feature_workout.domain.model.helper.RoutineWithExerciseCount
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WeightUnit
import com.example.workouttrackerapp.feature_workout.domain.use_case.ExerciseUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.RecordUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.RoutineExerciseUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.RoutineUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.WorkoutExerciseUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.WorkoutUseCases
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components.RecordDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditWorkoutViewModel @Inject constructor(
    private val workoutUseCases: WorkoutUseCases,
    private val exerciseUseCases: ExerciseUseCases,
    private val workoutExerciseUseCases: WorkoutExerciseUseCases,
    private val recordUseCases: RecordUseCases,
    private val routineUseCases: RoutineUseCases,
    private val routineExerciseUseCases: RoutineExerciseUseCases,
    savedStateHandle: SavedStateHandle,
    dataStoreManager: AppModule.DataStoreManager
) : ViewModel() {
    private val _workoutNote = mutableStateOf(
        WorkoutTextFieldState(
            hint = "Enter a note"
        )
    )
    val workoutNote: State<WorkoutTextFieldState> = _workoutNote

    private val _searchQuery = mutableStateOf(
        WorkoutTextFieldState(
            hint = "Search for exercise..."
        )
    )
    val searchQuery: State<WorkoutTextFieldState> = _searchQuery

    private val _workoutTimestamp = mutableStateOf<Long?>(System.currentTimeMillis())
    val workoutTimestamp: State<Long?> = _workoutTimestamp

    private val _workoutColor = mutableStateOf((0..<Workout.workoutColors.size).random())
    val workoutColor: State<Int> = _workoutColor

    private val _isDatePickerVisible = mutableStateOf(false)
    val isDatePickerVisible: State<Boolean> = _isDatePickerVisible

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var currentWorkoutId: Int? = null

    private val _exerciseList = mutableStateOf<List<Exercise>>(emptyList())
    val exerciseList: State<List<Exercise>> = _exerciseList

    private val _queriedExerciseList = mutableStateOf<List<Exercise>>(emptyList())
    val queriedExerciseList: State<List<Exercise>> = _queriedExerciseList

    private val _isAddExerciseModalVisible = mutableStateOf(false)
    val isAddExerciseModalVisible: State<Boolean> = _isAddExerciseModalVisible

    private val _isChangeExerciseModalVisible = mutableStateOf(false)
    val isChangeExerciseModalVisible: State<Boolean> = _isChangeExerciseModalVisible

    private val _isImportRoutineModalVisible = mutableStateOf(false)
    val isImportRoutineModalVisible: State<Boolean> = _isImportRoutineModalVisible

    private val _isEditRecordModalVisible = mutableStateOf(false)
    val isEditRecordModalVisible: State<Boolean> = _isEditRecordModalVisible

    private val _isOverviewOptionsModalVisible = mutableStateOf(false)
    val isOverviewOptionsModalVisible: State<Boolean> = _isOverviewOptionsModalVisible

    private val _isExerciseOptionsModalVisible = mutableStateOf(false)
    val isExerciseOptionsModalVisible: State<Boolean> = _isExerciseOptionsModalVisible

    private val _isExerciseNoteModalVisible = mutableStateOf(false)
    val isExerciseNoteModalVisible: State<Boolean> = _isExerciseNoteModalVisible

    private val _isDeleteConfirmationModalVisible = mutableStateOf(false)
    val isDeleteConfirmationModalVisible: State<Boolean> = _isDeleteConfirmationModalVisible

    private val _isBackHandlerDeleteConfirmationModalVisible = mutableStateOf(false)
    val isBackHandlerDeleteConfirmationModalVisible: State<Boolean> =
        _isBackHandlerDeleteConfirmationModalVisible

    private val _currentWorkoutExercise = mutableStateOf<WorkoutExercise?>(null)
    val currentWorkoutExercise: State<WorkoutExercise?> = _currentWorkoutExercise

    private val _currentRecord = mutableStateOf<Record?>(null)
    val currentRecord: State<Record?> = _currentRecord

    private val _currentRecordDetails = mutableStateOf<RecordDetails?>(null)
    val currentRecordDetails: State<RecordDetails?> = _currentRecordDetails

    private val _recordsList = mutableStateOf<List<Record>>(emptyList())
    val recordsList: State<List<Record>> = _recordsList

    private val _exerciseRecordsMap = mutableStateMapOf<Int, List<Record>>()
    val exerciseRecordsMap: SnapshotStateMap<Int, List<Record>> = _exerciseRecordsMap

    private val _routinesListState = mutableStateOf<List<RoutineWithExerciseCount>>(emptyList())
    val routinesListState: State<List<RoutineWithExerciseCount>> = _routinesListState

    private val _workoutExerciseList = mutableStateOf<List<WorkoutExercise>>(emptyList())
    val workoutExerciseList: State<List<WorkoutExercise>> = _workoutExerciseList

    private val _expandedStates = mutableStateMapOf<Int, Boolean>()
    val expandedStates: SnapshotStateMap<Int, Boolean> = _expandedStates

    private val _weightUnitPref = mutableStateOf(WeightUnit.KG.displayName)
    val weightUnitPref: State<String> = _weightUnitPref

    init {
        savedStateHandle.toRoute<AddEditWorkout>().workoutId.let { workoutId ->
            Log.d("AddEditWorkoutViewModel", "Workout Id: $workoutId")
            if (workoutId != -1) {
                viewModelScope.launch {
                    workoutUseCases.getWorkout(workoutId)?.also { workout ->
                        Log.d("AddEditWorkoutViewModel", "Getting Workout")
                        currentWorkoutId = workout.id
                        _workoutColor.value = workout.color
                        _workoutTimestamp.value = workout.timestamp
                        _workoutNote.value = workoutNote.value.copy(
                            text = workout.note ?: "", isHintVisible = workout.note.isNullOrBlank()
                        )
                    }
                }
                viewModelScope.launch {
                    exerciseUseCases.getExercises().collect { exercises ->
                        Log.d("AddEditWorkoutViewModel", "Getting Exercises")
                        _exerciseList.value = exercises
                    }
                }
                viewModelScope.launch {
                    routineUseCases.getRoutinesWithExerciseCount().collect { routineList ->
                        _routinesListState.value = routineList
                    }
                }
                viewModelScope.launch {
                    workoutExerciseUseCases.getWorkoutExercises(workoutId)
                        .collect { workoutExercises ->
                            Log.d("AddEditWorkoutViewModel", "Getting WorkoutExercises")
                            _workoutExerciseList.value = workoutExercises
                            savedStateHandle.toRoute<AddEditWorkout>().exerciseId.let { exerciseId ->
                                workoutExercises.forEach { item ->
                                    if (item.exerciseId == exerciseId) {
                                        Log.d(
                                            "AddEditWorkoutViewModel",
                                            "WorkoutExercise ${item.id} contains exercise of ID: $exerciseId"
                                        )
                                        toggleWorkoutExerciseCardExpand(item.id!!)
                                    }

                                    _exerciseRecordsMap[item.exerciseId] =
                                        _exerciseRecordsMap.getOrDefault(
                                            item.exerciseId,
                                            recordUseCases.getLatestExerciseRecords(
                                                item.exerciseId, _workoutTimestamp.value!!
                                            )
                                        )
                                }
                            }
                            Log.d(
                                "AddEditWorkoutViewModel",
                                "exerciseRecordsMap: ${_exerciseRecordsMap.toString()}"
                            )
                        }
                }
                viewModelScope.launch {
                    recordUseCases.getRecordsByWorkoutId(workoutId).collect { records ->
                        Log.d("AddEditWorkoutViewModel", "Getting Records")
                        _recordsList.value = records
                    }
                }
                viewModelScope.launch {
                    _weightUnitPref.value =
                        WeightUnit.fromValue(dataStoreManager.weightUnitPref.first())?.displayName
                            ?: WeightUnit.KG.displayName
                }
            }
        }
    }

    private fun toggleAddExerciseModal() {
        _isAddExerciseModalVisible.value = !isAddExerciseModalVisible.value
        _searchQuery.value = searchQuery.value.copy(
            text = ""
        )
        _queriedExerciseList.value = exerciseList.value
    }

    private fun toggleEditRecordModal() {
        _isEditRecordModalVisible.value = !isEditRecordModalVisible.value
    }

    private fun toggleWorkoutExerciseCardExpand(value: Int) {
        viewModelScope.launch {
            _expandedStates[value] = !_expandedStates.getOrDefault(value, false)
        }
        Log.d("AddEditWorkoutViewModel", _expandedStates.toString())
    }

    private fun toggleExerciseOptionsModal() {
        _isExerciseOptionsModalVisible.value = !isExerciseOptionsModalVisible.value
    }

    private fun toggleEditExerciseNoteModal() {
        _isExerciseNoteModalVisible.value = !isExerciseNoteModalVisible.value
    }

    private fun toggleImportRoutineModal() {
        _isImportRoutineModalVisible.value = !_isImportRoutineModalVisible.value
    }

    private fun toggleChangeExerciseModal() {
        _searchQuery.value = searchQuery.value.copy(
            text = ""
        )
        _queriedExerciseList.value = exerciseList.value
        _isChangeExerciseModalVisible.value = !_isChangeExerciseModalVisible.value

    }

    private fun addExerciseToWorkout(exerciseId: Int) {
        viewModelScope.launch {
            val insertedId = workoutExerciseUseCases.addWorkoutExercise(
                WorkoutExercise(
                    workoutId = currentWorkoutId!!, exerciseId = exerciseId, note = null
                )
            )
            recordUseCases.addRecord(
                Record(
                    workoutExerciseId = insertedId.toInt(), rep = 10, weight = null
                )
            )
            toggleWorkoutExerciseCardExpand(insertedId.toInt())
        }
    }

    private fun changeExercise(exerciseId: Int) {
        viewModelScope.launch {
            if (currentWorkoutExercise.value != null) {
                val updatedWorkoutExercise = WorkoutExercise(
                    id = currentWorkoutExercise.value!!.id,
                    workoutId = currentWorkoutExercise.value!!.workoutId,
                    exerciseId = exerciseId,
                    note = currentWorkoutExercise.value!!.note
                )

                workoutExerciseUseCases.updateWorkoutExercise(
                    updatedWorkoutExercise
                )
                _currentWorkoutExercise.value = updatedWorkoutExercise
            }
        }
    }

    fun onEvent(event: AddEditWorkoutEvent) {
        when (event) {
            is AddEditWorkoutEvent.EnteredNote -> {
                _workoutNote.value = workoutNote.value.copy(
                    text = event.value
                )
            }

            is AddEditWorkoutEvent.EnteredTimestamp -> {
                _workoutTimestamp.value = event.value
            }

            is AddEditWorkoutEvent.ChangeNoteFocus -> {
                _workoutNote.value = _workoutNote.value.copy(
                    isHintVisible = !event.focusState.isFocused && workoutNote.value.text.isBlank()
                )
            }

            is AddEditWorkoutEvent.EnteredSearch -> {
                _searchQuery.value = searchQuery.value.copy(
                    text = event.value
                )
                _queriedExerciseList.value = exerciseList.value.filter { item ->
                    item.name.contains(event.value, ignoreCase = true)
                }
            }

            is AddEditWorkoutEvent.ChangeColor -> {
                _workoutColor.value = event.color
            }

            is AddEditWorkoutEvent.ToggleCardExpand -> {
                toggleWorkoutExerciseCardExpand(event.value)
                Log.d("AddEditWorkoutViewModel", expandedStates.toString())
            }

            is AddEditWorkoutEvent.SaveWorkout -> {
                viewModelScope.launch {
                    workoutUseCases.addWorkout(
                        Workout(id = currentWorkoutId,
                            timestamp = _workoutTimestamp.value!!,
                            color = _workoutColor.value,
                            note = _workoutNote.value.text.ifBlank { null })
                    )
                    _eventFlow.emit(UiEvent.SaveWorkout)
                }
            }

            is AddEditWorkoutEvent.ToggleDatePicker -> {
                _isDatePickerVisible.value = !isDatePickerVisible.value
            }

            is AddEditWorkoutEvent.ToggleAddExerciseModal -> {
                toggleAddExerciseModal()
            }

            is AddEditWorkoutEvent.AddExerciseToWorkout -> {
                Log.d("AddEditWorkoutViewModel", "Exercise Id: " + event.value)
                addExerciseToWorkout(event.value)
                toggleAddExerciseModal()
            }

            is AddEditWorkoutEvent.AddSetToExercise -> {
                viewModelScope.launch {
                    recordUseCases.addRecord(
                        Record(
                            workoutExerciseId = event.workoutExerciseId,
                            rep = event.prevRecord?.rep ?: 10,
                            weight = event.prevRecord?.weight
                        )
                    )
                }
            }

            is AddEditWorkoutEvent.DeleteWorkoutExercise -> {
                viewModelScope.launch {
                    workoutExerciseUseCases.deleteWorkoutExercise(event.value)
                }
                toggleExerciseOptionsModal()
                _expandedStates.remove(event.value.id)
            }

            is AddEditWorkoutEvent.ToggleEditSetModal -> {
                toggleEditRecordModal()

                if (event.value == null) {
                    _currentRecord.value = null
                    _currentRecordDetails.value = null
                } else {
                    _currentRecord.value = event.value
                    _currentRecordDetails.value = RecordDetails(
                        event.value.weight, event.value.rep, event.value.isWarmup
                    )
                }
            }

            is AddEditWorkoutEvent.UpdateRecord -> {
                viewModelScope.launch {
                    _currentRecord.value?.let {
                        recordUseCases.updateRecord(
                            it.copy(
                                weight = currentRecordDetails.value?.weight,
                                rep = currentRecordDetails.value?.rep,
                                isWarmup = currentRecordDetails.value!!.isWarmup
                            )
                        )
                    }
                }
                toggleEditRecordModal()
            }

            is AddEditWorkoutEvent.UpdateRecordDetails -> {
                _currentRecordDetails.value = event.value
            }

            is AddEditWorkoutEvent.ToggleEditNoteModal -> {
                toggleEditExerciseNoteModal()
                toggleExerciseOptionsModal()
            }

            is AddEditWorkoutEvent.SaveExerciseNote -> {
                viewModelScope.launch {
                    currentWorkoutExercise.value?.id?.let {
                        workoutExerciseUseCases.updateWorkoutExerciseNote(it, event.value?.ifEmpty {
                            null
                        })
                    }
                }
                toggleExerciseOptionsModal()
            }

            is AddEditWorkoutEvent.DeleteRecord -> {
                viewModelScope.launch {
                    _currentRecord.value?.let { recordUseCases.deleteRecord(it) }
                }
            }

            is AddEditWorkoutEvent.ToggleExerciseOptionsModal -> {
                if (event.value != null) {
                    _currentWorkoutExercise.value =
                        workoutExerciseList.value.find { it.id == event.value }
                } else {
                    _currentWorkoutExercise.value = null
                }
                toggleExerciseOptionsModal()
            }

            is AddEditWorkoutEvent.ToggleImportRoutineModal -> toggleImportRoutineModal()

            is AddEditWorkoutEvent.AddRoutineExercisesToWorkout -> {
                viewModelScope.launch(Dispatchers.IO) {
                    routineExerciseUseCases.getRoutineExercises(event.routineId).forEach { item ->
                        addExerciseToWorkout(item.exerciseId)
                    }
                }
                toggleImportRoutineModal()
            }

            AddEditWorkoutEvent.ToggleOverviewOptionsModal -> {
                _isOverviewOptionsModalVisible.value = !_isOverviewOptionsModalVisible.value
            }

            AddEditWorkoutEvent.DeleteWorkout -> {
                if (_isDeleteConfirmationModalVisible.value) {
                    _isDeleteConfirmationModalVisible.value =
                        !_isDeleteConfirmationModalVisible.value
                }
                viewModelScope.launch {
                    workoutUseCases.deleteWorkout(
                        Workout(
                            currentWorkoutId, 0, 0, null
                        )
                    )
                }
            }

            AddEditWorkoutEvent.ToggleDeleteConfirmationModal -> {
                _isDeleteConfirmationModalVisible.value = !_isDeleteConfirmationModalVisible.value
            }

            AddEditWorkoutEvent.ToggleBackHandlerWorkoutDeleteModal -> {
                _isBackHandlerDeleteConfirmationModalVisible.value =
                    !_isBackHandlerDeleteConfirmationModalVisible.value
            }

            is AddEditWorkoutEvent.ChangeExercise -> {
                changeExercise(event.value)
                toggleChangeExerciseModal()
            }

            is AddEditWorkoutEvent.ToggleChangeExerciseModal -> {
                toggleChangeExerciseModal()
            }
        }

    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        object SaveWorkout : UiEvent()
    }
}