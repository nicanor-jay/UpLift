package com.example.workouttrackerapp.feature_workout.domain.model.helper

data class RecordContribution(
    val formattedDate: String?,
    val monthLabel: String?,
    val hasContribution: Boolean,
    val record: ProcessedRecordsWithTimestamp? = null,
)
