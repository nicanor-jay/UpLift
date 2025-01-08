package com.example.workouttrackerapp.feature_workout.presentation.add_edit_routine

sealed class AddEditRoutineEvent {
    data class EnteredSearch(val value: String) : AddEditRoutineEvent()
    data class AddExerciseToRoutine(val exerciseId: Int) : AddEditRoutineEvent()
    data class EnteredRoutineTitle(val value: String) : AddEditRoutineEvent()
    data class RemoveRoutineExercise(val value: Int) : AddEditRoutineEvent()

    data object ToggleSelectExerciseModal : AddEditRoutineEvent()
    data object SaveRoutine : AddEditRoutineEvent()
    data object DeleteRoutine : AddEditRoutineEvent()
    data object ToggleBackHandlerWorkoutDeleteModal : AddEditRoutineEvent()
}