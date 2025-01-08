package com.example.workouttrackerapp.feature_workout.presentation.progression

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.workouttrackerapp.di.AppModule
import com.example.workouttrackerapp.feature_workout.domain.model.Exercise
import com.example.workouttrackerapp.feature_workout.domain.model.helper.ProcessedRecordsWithTimestamp
import com.example.workouttrackerapp.feature_workout.domain.model.helper.RecordContribution
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WeekStartDay
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WeightUnit
import com.example.workouttrackerapp.feature_workout.domain.use_case.ExerciseUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.RecordUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.WorkoutUseCases
import com.example.workouttrackerapp.feature_workout.domain.util.calendarToString
import com.example.workouttrackerapp.feature_workout.domain.util.getMonthName
import com.example.workouttrackerapp.feature_workout.domain.util.getOffset
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.WorkoutTextFieldState
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProgressionViewModel @Inject constructor(
    private val workoutUseCases: WorkoutUseCases,
    private val exerciseUseCases: ExerciseUseCases,
    private val recordUseCases: RecordUseCases,
    private val dataStoreManager: AppModule.DataStoreManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _isSelectExerciseModalVisible = mutableStateOf(false)
    val isSelectExerciseModalVisible: State<Boolean> = _isSelectExerciseModalVisible

    private val _selectedExercise = mutableStateOf<Exercise?>(null)
    val selectedExercise: State<Exercise?> = _selectedExercise

    private val _exerciseRecords = mutableStateOf<List<ProcessedRecordsWithTimestamp>?>(null)
    val exerciseRecords: State<List<ProcessedRecordsWithTimestamp>?> = _exerciseRecords

    private val _contributionRecords = mutableStateOf<List<RecordContribution>?>(null)
    val contributionRecords: State<List<RecordContribution>?> = _contributionRecords

    private val _exerciseList = mutableStateOf<List<Exercise>>(emptyList())
    private val exerciseList: State<List<Exercise>> = _exerciseList

    private val _queriedExerciseList = mutableStateOf<List<Exercise>>(emptyList())
    val queriedExerciseList: State<List<Exercise>> = _queriedExerciseList

    private val _weekStartDay = mutableStateOf<WeekStartDay>(WeekStartDay.SUNDAY)
    val weekStartDay: State<WeekStartDay> = _weekStartDay

    private val _weightUnit = mutableStateOf<WeightUnit>(WeightUnit.KG)
    val weightUnit : State<WeightUnit> = _weightUnit

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("d MMM")

    val modelProducer = CartesianChartModelProducer()

    private val xToDateMapKey = ExtraStore.Key<Map<Float, LocalDate>>()

    private val _checkboxStates = mutableStateOf(
        mapOf(
            CheckboxOption.OPTION_ONE to true,
            CheckboxOption.OPTION_TWO to true,
            CheckboxOption.OPTION_THREE to false
        )
    )

    val checkboxStates: State<Map<CheckboxOption, Boolean>> = _checkboxStates

    private val _valueFormatter = mutableStateOf(
        CartesianValueFormatter { context, x, _ ->
            val date = context.model.extraStore[xToDateMapKey].get(x.toFloat())
                ?: LocalDate.ofEpochDay(x.toLong())
            date.format(dateTimeFormatter)
        }
    )

    val valueFormatter: State<CartesianValueFormatter> = _valueFormatter

    private val _searchQuery = mutableStateOf(
        WorkoutTextFieldState(
            hint = "Search for exercise..."
        )
    )
    val searchQuery: State<WorkoutTextFieldState> = _searchQuery

    init {
        viewModelScope.launch {
            exerciseUseCases.getParticipatedExercises().collect { exercises ->
                Log.d("AddEditWorkoutViewModel", "Getting Exercises")
                _exerciseList.value = exercises
            }
        }
    }

    private fun toggleSelectExerciseModal() {
        _isSelectExerciseModalVisible.value = !isSelectExerciseModalVisible.value

    }

    private suspend fun updateGraph() {
        val recordList = _exerciseRecords.value
        if (!recordList.isNullOrEmpty()) {
            val intensityData = recordList.filter { it.avgWeight != null }.map {
                convertTimestampToLocalDate(it.date!!) to it.avgIntensity!!
            }.associate { it.copy() }

            val xToDates = intensityData.keys.associateBy { it.toEpochDay().toFloat() }

            modelProducer.runTransaction {
                lineSeries {
                    if (checkboxStates.value[CheckboxOption.OPTION_ONE]!!) {
                        series(xToDates.keys, recordList.map { it.avgWeight!! })
                    }
                    if (checkboxStates.value[CheckboxOption.OPTION_TWO]!!) {
                        series(xToDates.keys, recordList.map { it.avgReps!! })
                    }
                    if (checkboxStates.value[CheckboxOption.OPTION_THREE]!!) {
                        series(xToDates.keys, intensityData.values)
                    }
                    extras { extraStore ->
                        extraStore[xToDateMapKey] = xToDates
                    }
                }
            }
        }
    }

    private fun generateExhaustiveContributionList(records: List<ProcessedRecordsWithTimestamp>): List<RecordContribution> {
        // Sort records to get the earliest and latest dates
        val sortedRecords = records.sortedBy { it.date }
        val earliestDateMillis = sortedRecords.firstOrNull()?.date ?: return emptyList()
        val latestDateMillis = System.currentTimeMillis()
//        val latestDateMillis = sortedRecords.lastOrNull()?.date ?: earliestDateMillis
        val offSet = getOffset(earliestDateMillis, _weekStartDay.value)

        // Initialize Calendar instances for start and end dates
        val calendarStart = Calendar.getInstance().apply { time = Date(earliestDateMillis) }
        val calendarEnd = Calendar.getInstance().apply { time = Date(latestDateMillis) }

        // Create a map for quick lookup by day
        val recordByDate = records.associateBy { record ->
            val cal = Calendar.getInstance().apply { time = Date(record.date ?: 0L) }
            cal[Calendar.YEAR] to cal[Calendar.DAY_OF_YEAR]  // Year and day-of-year as unique key per day
        }

        // Prepare date formatter (dd-MM-yyyy)
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val exhaustiveList = mutableListOf<RecordContribution>()

        var counter = 0

        Log.d(
            "ProgressionViewModel",
            "Month Name: ${getMonthName(records[0].formattedDate!!, "dd/MM/yyyy")}"
        )

        exhaustiveList.add(
            RecordContribution(
                null,
                getMonthName(sortedRecords[0].formattedDate!!, "dd/MM/yyyy").substring(0, 3),
                false,
                record = null
            )
        )

        repeat(offSet) {
            exhaustiveList.add(
                RecordContribution(null, null, false)
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
                        RecordContribution(
                            formattedDate = null,
                            monthLabel = getMonthName(
                                calendarToString(tempCalendar),
                                "dd/MM/yyyy"
                            ).substring(0, 3),
                            hasContribution = false
                        )
                    )
                    Log.d(
                        "ProgressionViewModel",
                        "Month Change Placeholder Added at Counter $counter"
                    )
                } else {
                    // Otherwise, just add a regular placeholder as before
                    exhaustiveList.add(
                        RecordContribution(
                            formattedDate = null,
                            monthLabel = null,
                            hasContribution = false
                        )
                    )
                }
            }


            val dayKey = calendarStart[Calendar.YEAR] to calendarStart[Calendar.DAY_OF_YEAR]

            // Find the record for the current day, or create a placeholder if none exists
            val dailyRecord = recordByDate[dayKey]

            // Determine if this day had any valid contribution (non-null avgWeight, avgReps, avgIntensity)
            val hasContribution = dailyRecord?.let {
                it.avgWeight != null || it.avgReps != null || it.avgIntensity != null
            } ?: false

            // Create the formatted date string
            val formattedDate = dateFormatter.format(calendarStart.time)

            // Add the contribution record with formatted date and hasContribution flag
            exhaustiveList.add(
                RecordContribution(
                    formattedDate = formattedDate,
                    null,
                    hasContribution = hasContribution,
                    record = dailyRecord
                )
            )

            // Move to the next day
            calendarStart.add(Calendar.DAY_OF_YEAR, 1)
            counter += 1
            Log.d("ProgressionViewModel", "Ding $counter")
        }

        return exhaustiveList
    }

    fun onEvent(event: ProgressionEvent) {
        when (event) {
            is ProgressionEvent.SelectExercise -> {
                _selectedExercise.value = exerciseList.value.find {
                    it.id == event.value
                }
                toggleSelectExerciseModal()
                viewModelScope.launch {
                    combine(
                        recordUseCases.getRecordsWithTimeByExerciseId(event.value),
                        dataStoreManager.weekStartDay,
                        dataStoreManager.weightUnitPref
                    ) { list, weekStartDay, weightUnit ->
                        _exerciseRecords.value = list
                        _weekStartDay.value =
                            WeekStartDay.fromValue(weekStartDay) ?: WeekStartDay.SUNDAY
                        _weightUnit.value = WeightUnit.fromValue(weightUnit) ?: WeightUnit.KG
                        updateGraph()
                        _contributionRecords.value = generateExhaustiveContributionList(list)
                        Log.d("ProgressionViewModel", list.toString())
                        Log.d(
                            "ProgressionViewModel",
                            generateExhaustiveContributionList(list).toString()
                        )
                    }.collect {}
                }
            }

            is ProgressionEvent.ToggleSelectExerciseModal -> {
                toggleSelectExerciseModal()
                _searchQuery.value = searchQuery.value.copy(
                    text = ""
                )
                _queriedExerciseList.value = exerciseList.value
            }

            is ProgressionEvent.EnteredSearch -> {
                _searchQuery.value = searchQuery.value.copy(
                    text = event.value
                )
                _queriedExerciseList.value = exerciseList.value.filter { item ->
                    item.name.contains(event.value, ignoreCase = true)
                }
            }

            is ProgressionEvent.ToggleCheckbox -> {
                viewModelScope.launch {
                    _checkboxStates.value = checkboxStates.value.toMutableMap().apply {
                        val currentCheckedCount = this.count { it.value }

                        // Only update if toggling won't leave all checkboxes unchecked
                        if (event.isChecked || currentCheckedCount > 1) {
                            this[event.value] = event.isChecked
                        }
                    }
                    updateGraph()
                }
            }

        }
    }
}


enum class CheckboxOption(val label: String) {
    OPTION_ONE("Weight"),
    OPTION_TWO("Reps"),
    OPTION_THREE("Intensity")
}