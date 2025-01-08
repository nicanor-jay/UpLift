package com.example.workouttrackerapp.feature_workout.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Routine(
    @PrimaryKey val id: Int? = null,
    val name: String,
)