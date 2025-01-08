package com.example.workouttrackerapp.feature_workout.domain.repository

import com.example.workouttrackerapp.feature_workout.domain.model.WorkoutExercise
import kotlinx.coroutines.flow.Flow

interface WorkoutExerciseRepository {
    fun getWorkoutExerciseByWorkoutId(workoutId: Int): Flow<List<WorkoutExercise>>
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long
    suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise)
    suspend fun updateWorkoutExerciseNote(id: Int, note: String?)
    suspend fun deleteExercise(workoutExercise: WorkoutExercise)
}