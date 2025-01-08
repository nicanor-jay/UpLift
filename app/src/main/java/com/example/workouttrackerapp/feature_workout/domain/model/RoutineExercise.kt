package com.example.workouttrackerapp.feature_workout.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Routine::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"]
        )
    ],
    indices = [Index("routineId"), Index("exerciseId")]
)
data class RoutineExercise(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val routineId: Int?,
    val exerciseId: Int
)
