package com.example.workouttrackerapp.feature_workout.domain.use_case.routine

import com.example.workouttrackerapp.feature_workout.domain.model.Routine
import com.example.workouttrackerapp.feature_workout.domain.repository.RoutineRepository

class GetRoutine(
    private val repository: RoutineRepository
) {
    suspend operator fun invoke(routineId: Int): Routine? {
        return repository.getRoutine(routineId)
    }
}