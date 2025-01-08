package com.example.workouttrackerapp.feature_workout.presentation.profile

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttrackerapp.di.AppModule
import com.example.workouttrackerapp.feature_workout.domain.model.Exercise
import com.example.workouttrackerapp.feature_workout.domain.model.Workout
import com.example.workouttrackerapp.feature_workout.domain.model.helper.ProcessedWorkoutsWithTimestamp
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WeekStartDay
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WorkoutContribution
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WorkoutWithExerciseCount
import com.example.workouttrackerapp.feature_workout.domain.use_case.ExerciseUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.WorkoutUseCases
import com.example.workouttrackerapp.feature_workout.domain.util.calendarToString
import com.example.workouttrackerapp.feature_workout.domain.util.getMonthName
import com.example.workouttrackerapp.feature_workout.domain.util.getOffset
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.AddEditWorkoutViewModel.UiEvent
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.WorkoutTextFieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val workoutUseCases: WorkoutUseCases,
    private val exerciseUseCases: ExerciseUseCases,
    private val dataStoreManager: AppModule.DataStoreManager,
) : ViewModel() {

    private val _workoutList = mutableStateOf<List<WorkoutWithExerciseCount>>(emptyList())
    val workoutList: State<List<WorkoutWithExerciseCount>> = _workoutList

    private val _workoutContributionList = mutableStateOf<List<WorkoutContribution>>(emptyList())
    val workoutContributionList: State<List<WorkoutContribution>> = _workoutContributionList

    private val _currentStreak = mutableStateOf(0)
    val currentStreak: State<Int> = _currentStreak

    private val _isExerciseListModalVisible = mutableStateOf(false)
    val isExerciseListModalVisible: State<Boolean> = _isExerciseListModalVisible

    private val _isWeeklyTargetModalVisible = mutableStateOf(false)
    val isWeeklyTargetModalVisible: State<Boolean> = _isWeeklyTargetModalVisible

    private val _currentWeeklyTarget =
        mutableStateOf(AppModule.DataStoreManager.DEFAULT_WEEKLY_WORKOUT_TARGET)
    val currentWeeklyTarget: State<Int> = _currentWeeklyTarget

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

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading

    private val _weekStartDay = mutableStateOf<WeekStartDay>(WeekStartDay.SUNDAY)
    val weekStartDay: State<WeekStartDay> = _weekStartDay

    init {
        viewModelScope.launch {
            combine(
                workoutUseCases.getProcessedWorkouts(),
                dataStoreManager.weeklyWorkoutTarget,
                dataStoreManager.weekStartDay
            ) { processedWorkouts, target, weekStartDay ->
                _weekStartDay.value = WeekStartDay.fromValue(weekStartDay) ?: WeekStartDay.SUNDAY
                val exhaustiveList = generateExhaustiveContributionList(processedWorkouts)
                _workoutContributionList.value = exhaustiveList
                _currentWeeklyTarget.value = target
                calculateStreak(exhaustiveList, target)
            }.collect { streak ->
                _currentStreak.value = streak
            }
        }
        viewModelScope.launch {
            workoutUseCases.getWorkouts().collect { workoutList ->
                _workoutList.value = workoutList
            }
        }
    }

    private fun generateExhaustiveContributionList(workouts: List<ProcessedWorkoutsWithTimestamp>): List<WorkoutContribution> {
        _isLoading.value = true

        val sortedWorkouts = workouts.sortedBy { it.date }
        val currentTimeMillis = System.currentTimeMillis()

        // Initialize `earliestDateMillis` based on the presence of workouts
        val earliestDateMillis = if (sortedWorkouts.isEmpty()) {
            Calendar.getInstance().apply {
                add(Calendar.MONTH, -6) // Default to 1 year ago
            }.timeInMillis
        } else {
            val firstWorkoutDate = sortedWorkouts.first().date!!
            val timeDifference = currentTimeMillis - firstWorkoutDate

            Calendar.getInstance().apply {
                timeInMillis = firstWorkoutDate
                if (timeDifference < 2 * 30 * 24 * 60 * 60 * 1000L) { // Less than 2 months
                    add(Calendar.MONTH, -2) // Subtract 2 month
                }
            }.timeInMillis
        }

        val latestDateMillis = System.currentTimeMillis()
        val offSet = getOffset(earliestDateMillis, _weekStartDay.value)


        // Initialize Calendar instances for start and end dates
        val calendarStart = Calendar.getInstance().apply { time = Date(earliestDateMillis) }
        val calendarEnd = Calendar.getInstance().apply { time = Date(latestDateMillis) }

        // If there are no workouts, simulate 3 months of data
//        if (workouts.isEmpty()) {
//            calendarStart.timeInMillis = Calendar.getInstance().apply {
//                add(Calendar.MONTH, -2) // Go back 3 months
//                set(Calendar.DAY_OF_MONTH, 1) // Start at the first of the month
//            }.timeInMillis
//            calendarEnd.timeInMillis = System.currentTimeMillis()
//        }

        // Create a map for quick lookup by day
        val workoutByDate = workouts.groupBy { workout ->
            val cal = Calendar.getInstance().apply { time = Date(workout.date ?: 0L) }
            cal[Calendar.YEAR] to cal[Calendar.DAY_OF_YEAR]
        }

        // Prepare date formatter (dd-MM-yyyy)
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val exhaustiveList = mutableListOf<WorkoutContribution>()

        var counter = 0

        exhaustiveList.add(
            WorkoutContribution(
                null,
                getMonthName(dateFormatter.format(calendarStart.time), "dd/MM/yyyy").substring(
                    0,
                    3
                ),
                false,
                workoutList = null
            )
        )

        repeat(offSet) {
            exhaustiveList.add(
                WorkoutContribution(null, null, false, null)
            )
        }

        while (calendarStart.before(calendarEnd) || calendarStart[Calendar.DAY_OF_YEAR] == calendarEnd[Calendar.DAY_OF_YEAR]) {
            if ((counter + offSet) % 7 == 0 && counter > 0) {
                // Create a temporary calendar to check the month change in the next 7 days
                val tempCalendar = calendarStart.clone() as Calendar
                tempCalendar.add(Calendar.DAY_OF_YEAR, 7)

                // Check if the month will change within the next 7 days
                val monthWillChange = calendarStart[Calendar.MONTH] != tempCalendar[Calendar.MONTH]

                // If the month will change, add a special placeholder record
                if (monthWillChange) {
                    exhaustiveList.add(
                        WorkoutContribution(
                            formattedDate = null,
                            monthLabel = getMonthName(
                                calendarToString(tempCalendar),
                                "dd/MM/yyyy"
                            ).substring(0, 3),
                            hasContribution = false,
                            workoutList = null
                        )
                    )
                } else {
                    // Otherwise, just add a regular placeholder as before
                    exhaustiveList.add(
                        WorkoutContribution(
                            formattedDate = null,
                            monthLabel = null,
                            hasContribution = false,
                            workoutList = null
                        )
                    )
                }
            }

            val dayKey = calendarStart[Calendar.YEAR] to calendarStart[Calendar.DAY_OF_YEAR]

            // Find the record for the current day, or create a placeholder if none exists
            val dailyWorkout = workoutByDate[dayKey]

            // Determine if this day had any valid contribution
            val hasContribution = dailyWorkout?.any {
                it.numExercises != null && it.numExercises > 0
            } ?: false

            // Create the formatted date string
            val formattedDate = dateFormatter.format(calendarStart.time)

            // Add the contribution record with formatted date and hasContribution flag
            exhaustiveList.add(
                WorkoutContribution(
                    formattedDate = formattedDate,
                    null,
                    hasContribution = hasContribution,
                    workoutList = dailyWorkout
                )
            )

            // Move to the next day
            calendarStart.add(Calendar.DAY_OF_YEAR, 1)
            counter += 1
        }
        _isLoading.value = false

        return exhaustiveList
    }

    private fun calculateStreak(
        contributions: List<WorkoutContribution>,
        targetWorkoutsPerWeek: Int
    ): Int {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val contributionsByWeek = mutableMapOf<Int, MutableMap<DayOfWeek, Int>>()
        val today = LocalDate.now()
        var streak = 0

        contributions.forEach { contribution ->
            val formattedDate = contribution.formattedDate
            val workoutList = contribution.workoutList

            if (formattedDate != null) {
                val date = LocalDate.parse(formattedDate, formatter)
                val yearWeek = date.getYearWeek() // Helper extension function defined below
                val dayOfWeek = date.dayOfWeek

                val dailyWorkouts = workoutList?.size ?: 0
                contributionsByWeek.getOrPut(yearWeek) { mutableMapOf() }
                    .merge(dayOfWeek, dailyWorkouts, Int::plus)
            }
        }

        // Traverse weekly contributions in reverse order to determine the streak
        val sortedWeeks = contributionsByWeek.keys.sortedDescending()
        for (week in sortedWeeks) {
            val weeklyWorkoutCount = contributionsByWeek[week]?.values?.sum() ?: 0
            if (weeklyWorkoutCount >= targetWorkoutsPerWeek) {
                streak++
            } else if (week < today.getYearWeek()) {
                // Stop if a previous week's target is not met
                break
            }
        }

        return streak
    }

    // Helper extension function to get a "Year-Week" identifier
    private fun LocalDate.getYearWeek(): Int {
        val weekFields = java.time.temporal.WeekFields.ISO
        return this.get(weekFields.weekBasedYear()) * 100 + this.get(weekFields.weekOfWeekBasedYear())
    }

    private fun loadExercises() {
        viewModelScope.launch {
            exerciseUseCases.getExercises().collect { exerciseList ->
                _exerciseList.value = exerciseList
                _queriedExerciseList.value = exerciseList
            }
        }
    }

    private fun toggleExerciseModal() {
        _isExerciseListModalVisible.value = !_isExerciseListModalVisible.value

    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.InsertNewWorkout -> {
                viewModelScope.launch {
                    val insertedId = workoutUseCases.addWorkout(
                        Workout(
                            null, System.currentTimeMillis(), 0, null
                        )
                    )

                    _eventFlow.emit(UiEvent.InsertedNewWorkout(insertedId!!.toInt()))
                }
            }

            is ProfileEvent.ToggleExerciseModal -> {
                if (!_isExerciseListModalVisible.value) {
                    loadExercises()
                    _queriedExerciseList.value = _exerciseList.value
                } else {
                    _exerciseList.value = emptyList()
                }

                toggleExerciseModal()
                _searchQuery.value = searchQuery.value.copy(
                    text = ""
                )
            }

            is ProfileEvent.EnteredSearch -> {
                _searchQuery.value = searchQuery.value.copy(
                    text = event.value
                )
                _queriedExerciseList.value = exerciseList.value.filter { item ->
                    item.name.contains(event.value, ignoreCase = true)
                }
            }

            is ProfileEvent.EnteredWeeklyTarget -> {
                viewModelScope.launch {
                    dataStoreManager.setWeeklyWorkoutTarget(event.weeklyTarget)
                }
            }

            is ProfileEvent.ToggleWeeklyTargetModal -> {
                _isWeeklyTargetModalVisible.value = !_isWeeklyTargetModalVisible.value
            }
        }
    }

    sealed class UiEvent {
        data class InsertedNewWorkout(val insertedId: Int) : UiEvent()
    }

}