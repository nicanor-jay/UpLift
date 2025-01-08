package com.example.workouttrackerapp.feature_workout.domain.model.helper

data class WorkoutWithExerciseCount(
    val workoutId: Int,
    val timestamp: Long,
    val color: Int,
    val note: String?,
    val exerciseCount: Int
)