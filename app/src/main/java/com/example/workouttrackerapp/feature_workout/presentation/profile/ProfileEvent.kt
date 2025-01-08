package com.example.workouttrackerapp.feature_workout.presentation.profile

sealed class ProfileEvent {
    object InsertNewWorkout : ProfileEvent()
    object ToggleExerciseModal : ProfileEvent()
    object ToggleWeeklyTargetModal : ProfileEvent()
    data class EnteredWeeklyTarget(val weeklyTarget: Int) : ProfileEvent()
    data class EnteredSearch(val value: String) : ProfileEvent()
}