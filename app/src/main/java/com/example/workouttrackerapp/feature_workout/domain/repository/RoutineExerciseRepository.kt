package com.example.workouttrackerapp.feature_workout.domain.repository

import com.example.workouttrackerapp.feature_workout.domain.model.RoutineExercise
import kotlinx.coroutines.flow.Flow

interface RoutineExerciseRepository {
    suspend fun addRoutineExercise(routineExercise: RoutineExercise)
    suspend fun deleteRoutineExercise(routineExercise: RoutineExercise)
    suspend fun getRoutineExercisesFlow(routineId: Int): Flow<List<RoutineExercise>>
    fun getRoutineExercises(routineId: Int): List<RoutineExercise>
}