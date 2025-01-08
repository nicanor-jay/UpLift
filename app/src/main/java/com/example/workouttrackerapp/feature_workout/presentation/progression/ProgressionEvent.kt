package com.example.workouttrackerapp.feature_workout.presentation.progression

sealed class ProgressionEvent {
    data class SelectExercise(val value: Int) : ProgressionEvent()
    data class EnteredSearch(val value: String) : ProgressionEvent()
    data class ToggleCheckbox(val value: CheckboxOption, val isChecked: Boolean) :
        ProgressionEvent()

    data object ToggleSelectExerciseModal : ProgressionEvent()

}