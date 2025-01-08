package com.example.workouttrackerapp.feature_workout.domain.model.helper


data class ProcessedRecordsWithTimestamp(
    val date: Long?,
    val formattedDate: String?,
    val avgWeight: Float?,
    val avgReps: Float?,
    val avgIntensity: Float?,
    val workoutId: Int = -1,
    val workoutColor: Int = -1,
    val exerciseId: Int = -1,
)