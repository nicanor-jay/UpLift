package com.example.workouttrackerapp.feature_workout.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "exercise_catalog"
)
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String,
    val description: String,
)
