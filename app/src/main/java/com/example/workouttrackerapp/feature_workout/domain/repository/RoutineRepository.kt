package com.example.workouttrackerapp.feature_workout.domain.repository

import com.example.workouttrackerapp.feature_workout.domain.model.Routine
import com.example.workouttrackerapp.feature_workout.domain.model.helper.RoutineWithExerciseCount
import kotlinx.coroutines.flow.Flow

interface RoutineRepository {
    fun getRoutines(): Flow<List<Routine>>
    fun getRoutine(routineId: Int): Routine?
    fun getRoutinesWithExerciseCount(): Flow<List<RoutineWithExerciseCount>>
    suspend fun addRoutine(routine: Routine): Long
    suspend fun deleteRoutine(routine: Routine)
    fun updateRoutine(routine: Routine)
}