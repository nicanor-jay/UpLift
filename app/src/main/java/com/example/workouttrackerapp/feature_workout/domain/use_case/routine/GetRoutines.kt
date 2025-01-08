package com.example.workouttrackerapp.feature_workout.domain.use_case.routine

import com.example.workouttrackerapp.feature_workout.domain.model.Routine
import com.example.workouttrackerapp.feature_workout.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow

class GetRoutines(
    private val repository: RoutineRepository
) {
    operator fun invoke(): Flow<List<Routine>> {
        return repository.getRoutines()
    }
}