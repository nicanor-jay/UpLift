package com.example.workouttrackerapp.feature_workout.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = WorkoutExercise::class,
            parentColumns = ["id"],
            childColumns = ["workoutExerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutExerciseId")]
)
data class Record(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val workoutExerciseId: Int,
    val isWarmup: Boolean = false,
    val rep: Int?,
    val weight: Float?
)