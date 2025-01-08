package com.example.workouttrackerapp.feature_workout.domain.model.helper

data class ProcessedWorkoutsWithTimestamp(
    val workoutId: Int?,
    val date: Long?,
    val formattedDate: String?,
    val workoutColor: Int?,
    val numExercises: Int?
)