package com.example.workouttrackerapp.feature_workout.data.repository

import com.example.workouttrackerapp.feature_workout.data.data_source.RoutineExerciseDao
import com.example.workouttrackerapp.feature_workout.domain.model.RoutineExercise
import com.example.workouttrackerapp.feature_workout.domain.repository.RoutineExerciseRepository
import kotlinx.coroutines.flow.Flow

class RoutineExerciseRepositoryImpl(
    private val dao: RoutineExerciseDao
) : RoutineExerciseRepository {

    override suspend fun addRoutineExercise(routineExercise: RoutineExercise) {
        return dao.insertRoutineExercise(routineExercise)
    }

    override suspend fun deleteRoutineExercise(routineExercise: RoutineExercise) {
        return dao.deleteRoutineExercise(routineExercise)
    }

    override suspend fun getRoutineExercisesFlow(routineId: Int): Flow<List<RoutineExercise>> {
        return dao.getRoutineExercisesFlow(routineId)
    }

    override fun getRoutineExercises(routineId: Int): List<RoutineExercise> {
        return dao.getRoutineExercises(routineId)
    }
}