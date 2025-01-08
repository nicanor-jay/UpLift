package com.example.workouttrackerapp.feature_workout.data.repository

import com.example.workouttrackerapp.feature_workout.data.data_source.RoutineDao
import com.example.workouttrackerapp.feature_workout.domain.model.Routine
import com.example.workouttrackerapp.feature_workout.domain.model.helper.RoutineWithExerciseCount
import com.example.workouttrackerapp.feature_workout.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow

class RoutineRepositoryImpl(
    private val dao: RoutineDao
) : RoutineRepository {
    override fun getRoutines(): Flow<List<Routine>> {
        return dao.getRoutines()
    }

    override suspend fun addRoutine(routine: Routine): Long {
        return dao.addRoutine(routine)
    }

    override suspend fun deleteRoutine(routine: Routine) {
        return dao.deleteRoutine(routine)
    }

    override fun getRoutine(routineId: Int): Routine? {
        return dao.getRoutine(routineId)
    }

    override fun getRoutinesWithExerciseCount(): Flow<List<RoutineWithExerciseCount>> {
        return dao.getRoutinesWithExerciseCount()
    }

    override fun updateRoutine(routine: Routine) {
        return dao.updateRoutine(routine)
    }
}