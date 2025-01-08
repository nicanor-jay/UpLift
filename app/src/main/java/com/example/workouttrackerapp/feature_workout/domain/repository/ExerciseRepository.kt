package com.example.workouttrackerapp.feature_workout.domain.repository

import com.example.workouttrackerapp.feature_workout.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {

    fun getExercises(): Flow<List<Exercise>>
    fun getParticipatedExercises(): Flow<List<Exercise>>
}