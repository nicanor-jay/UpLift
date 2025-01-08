package com.example.workouttrackerapp.feature_workout.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"]
        )
    ],
    indices = [Index("workoutId"), Index("exerciseId")]
)
data class WorkoutExercise(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val workoutId: Int,
    val exerciseId: Int,
    val note: String?
)