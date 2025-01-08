package com.example.workouttrackerapp.feature_workout.domain.model.helper

data class WorkoutContribution(
    val formattedDate: String?,
    val monthLabel: String?,
    val hasContribution: Boolean,
    val workoutList: List<ProcessedWorkoutsWithTimestamp>?
)