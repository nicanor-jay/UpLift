package com.example.workouttrackerapp.feature_workout.domain.repository

import com.example.workouttrackerapp.feature_workout.domain.model.Workout
import com.example.workouttrackerapp.feature_workout.domain.model.helper.ProcessedWorkoutsWithTimestamp
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WorkoutWithExerciseCount
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {

    fun getWorkouts(): Flow<List<WorkoutWithExerciseCount>>

    fun getProcessedWorkouts(): Flow<List<ProcessedWorkoutsWithTimestamp>>

    fun getWidgetData(): Flow<List<Boolean>>

    suspend fun getWorkoutById(id: Int): Workout?

    suspend fun insertWorkout(workout: Workout): Long

    suspend fun deleteWorkout(workout: Workout)

    suspend fun upsertWorkout(workout: Workout): Long?

    suspend fun updateWorkout(workout: Workout)

}