package com.example.workouttrackerapp.feature_workout.domain.use_case.routine

import com.example.workouttrackerapp.feature_workout.domain.model.Routine
import com.example.workouttrackerapp.feature_workout.domain.repository.RoutineRepository

class UpdateRoutine(
    private val repository: RoutineRepository
) {
    operator fun invoke(routine: Routine) {
        return repository.updateRoutine(routine)
    }
}