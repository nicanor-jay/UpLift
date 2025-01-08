package com.example.workouttrackerapp.feature_workout.presentation.workouts

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttrackerapp.feature_workout.domain.model.Workout
import com.example.workouttrackerapp.feature_workout.domain.model.WorkoutExercise
import com.example.workouttrackerapp.feature_workout.domain.use_case.RecordUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.WorkoutExerciseUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.WorkoutUseCases
import com.example.workouttrackerapp.feature_workout.domain.util.OrderType
import com.example.workouttrackerapp.feature_workout.domain.util.WorkoutOrder
import com.example.workouttrackerapp.feature_workout.presentation.routine.RoutinesViewModel.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class WorkoutsViewModel @Inject constructor(
    private val workoutUseCases: WorkoutUseCases,
    private val workoutExerciseUseCases: WorkoutExerciseUseCases,
    private val recordUseCases: RecordUseCases,
) : ViewModel() {

    private val _state = mutableStateOf(WorkoutsState())
    val state: State<WorkoutsState> = _state

    private val _isDeleteWorkoutModalVisible = mutableStateOf<Boolean>(false)
    val isDeleteWorkoutModalVisible: State<Boolean> = _isDeleteWorkoutModalVisible

    private val _workoutIdToBeDeleted = mutableStateOf<Int?>(null)

    private var getWorkoutsJob: Job? = null

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        getWorkouts(WorkoutOrder.Date(OrderType.Descending))
    }

    private fun toggleDeleteWorkoutModal() {
        _isDeleteWorkoutModalVisible.value = !_isDeleteWorkoutModalVisible.value
    }

    fun onEvent(event: WorkoutsEvent) {
        when (event) {
            is WorkoutsEvent.DeleteWorkout -> {
                toggleDeleteWorkoutModal()
                viewModelScope.launch {
                    _eventFlow.emit(UiEvent.ShowSnackbar("Deleted workout"))
                    workoutUseCases.deleteWorkout(
                        Workout(
                            id = _workoutIdToBeDeleted.value,
                            timestamp = 0,
                            color = 0,
                            note = null,
                        )
                    )
                }
            }

            is WorkoutsEvent.AddWorkout -> {
                viewModelScope.launch {
                    val insertedId = workoutUseCases.addWorkout(
                        Workout(
                            null, System.currentTimeMillis(), 0, null
                        )
                    )

                    _eventFlow.emit(UiEvent.InsertedNewWorkout(insertedId!!.toInt()))
                }
            }

            is WorkoutsEvent.Order -> {
                if (state.value.workoutOrder::class == event.workoutOrder::class &&
                    state.value.workoutOrder.orderType == event.workoutOrder.orderType
                ) {
                    return
                }
                getWorkouts(event.workoutOrder)

            }

            is WorkoutsEvent.ToggleOrderSection -> {
                _state.value = state.value.copy(
                    isOrderSectionVisible = !state.value.isOrderSectionVisible
                )
            }

            is WorkoutsEvent.ToggleDeleteWorkoutModal -> {
                if (!_isDeleteWorkoutModalVisible.value) {
                    _workoutIdToBeDeleted.value = event.workoutId
                } else {
                    _workoutIdToBeDeleted.value = null
                }
                toggleDeleteWorkoutModal()
            }
        }
    }

    private fun getWorkouts(workoutOrder: WorkoutOrder) {
        getWorkoutsJob?.cancel()
        workoutUseCases.getWorkouts(workoutOrder)
            .onEach { workouts ->
                _state.value = state.value.copy(
                    workouts = workouts,
                    workoutOrder = workoutOrder
                )
            }
            .launchIn(viewModelScope)
    }


    ///////////// FAKE DATA ///////////////

    private val exerciseIds = listOf(1, 15, 32, 48, 9)
    private val numDays = 110

    private val mondayExerciseIds = listOf(5, 13, 49, 29)
    private val tuesdayExerciseIds = listOf(36, 34)
    private val thursdayExerciseIds = listOf(2, 14, 43, 52)
    private val fridayExerciseIds = listOf(32, 35, 55)

    private fun generateWorkoutTimestamps(): List<Long> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -3)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val timestamps = mutableListOf<Long>()
        for (i in 0 until numDays) {

            if (isWorkoutDay(calendar)) {
                if ((0..100).random() <= 90) {
                    // 90% chance not to miss a workout
                    timestamps.add(calendar.timeInMillis)
                }
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return timestamps
    }

    private fun isWorkoutDay(calendar: Calendar): Boolean {
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY, Calendar.TUESDAY, Calendar.THURSDAY, Calendar.FRIDAY -> true
            else -> false
        }
    }

    // Generate a list of Workouts
    fun generateWorkouts() {
        var workoutsList: MutableList<Workout> = mutableListOf()
        viewModelScope.launch {
            val timestamps = generateWorkoutTimestamps()
            timestamps.mapIndexed { index, timestamp ->
                val workout = Workout(
                    id = index + 1,
                    timestamp = timestamp,
                    color = (0..6).random(),  // Random color
                    note = "Workout note for workout $index"  // Optional note
                )

                workoutUseCases.addWorkout(
                    workout
                )
                workoutsList = workoutsList.plus(workout).toMutableList()
            }
            generateWorkoutExercises(workoutsList)
        }
    }

    // Generate a list of WorkoutExercises linked to Exercise ID: 1 for each workout
    fun generateWorkoutExercises(workouts: List<Workout>) {
        var workoutExerciseList: MutableList<WorkoutExercise> = mutableListOf()
        var counter = 1  // Start counter at 1 since the database is empty


        viewModelScope.launch {
            workouts.map { workout ->
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = workout.timestamp
                }
                // First WorkoutExercise with exerciseId 1

                val exerciseIds = when (calendar.get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY -> mondayExerciseIds
                    Calendar.TUESDAY -> tuesdayExerciseIds
                    Calendar.THURSDAY -> thursdayExerciseIds
                    Calendar.FRIDAY -> fridayExerciseIds
                    else -> emptyList()  // No exercises for other days
                }

                exerciseIds.forEach { eId ->
                    val workoutExercise = WorkoutExercise(
                        id = counter++,
                        workoutId = workout.id!!,
                        exerciseId = eId,
                        note = "Exercise note for workout ${workout.id} - Exercise 1"
                    )
                    workoutExerciseUseCases.addWorkoutExercise(
                        workoutExercise
                    )

                    // Update the list
                    workoutExerciseList = workoutExerciseList
                        .plus(workoutExercise)
                        .toMutableList()
                }
            }
            generateRecords(workoutExerciseList.toList())
        }
    }


    // Generate a list of Records for each WorkoutExercise
    fun generateRecords(workoutExercises: List<WorkoutExercise>): List<Record> {
        val records = mutableListOf<Record>()
        val minWeight = 10f  // Set the minimum weight value

        workoutExercises.forEach { workoutExercise ->
            viewModelScope.launch {

                val warmupSets = Random.nextInt(2, 3)
                val weight = minWeight + Random.nextFloat() * (100 - minWeight)
                // Add warmup sets half the time
                if ((0..100).random() <= 50) {
                    val reps = Random.nextInt(5, 15)  // Random reps between 5 and 15
                    repeat(warmupSets) {
                        recordUseCases.addRecord(
                            com.example.workouttrackerapp.feature_workout.domain.model.Record(
                                workoutExerciseId = workoutExercise.id!!,
                                rep = reps,
                                weight = weight * 0.75f,
                                isWarmup = true
                            )
                        )
                    }
                }


                // Working sets
                val additionalSets = warmupSets + Random.nextInt(1, 3)  // 0 to 2 additional records
                repeat(additionalSets) {
                    workoutExercise.id?.let {
                        recordUseCases.addRecord(
                            com.example.workouttrackerapp.feature_workout.domain.model.Record(
                                workoutExerciseId = workoutExercise.id,
                                rep = Random.nextInt(5, 15),
                                weight = weight
                            )
                        )
                    }
                }
            }
        }
        return records
    }

    sealed class UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent()
        data class InsertedNewWorkout(val insertedId: Int) : UiEvent()

    }
}