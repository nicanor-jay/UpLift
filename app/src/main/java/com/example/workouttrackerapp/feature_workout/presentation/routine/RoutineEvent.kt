package com.example.workouttrackerapp.feature_workout.presentation.routine

sealed class RoutineEvent {
    data object InsertRoutine : RoutineEvent()
    data object DeleteRoutine : RoutineEvent()
    data class ToggleDeleteRoutineModal(val routineId: Int?) : RoutineEvent()
}