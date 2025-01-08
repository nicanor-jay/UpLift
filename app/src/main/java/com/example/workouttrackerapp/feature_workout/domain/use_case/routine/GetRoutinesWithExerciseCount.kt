package com.example.workouttrackerapp.feature_workout.domain.use_case.routine

import com.example.workouttrackerapp.feature_workout.domain.model.helper.RoutineWithExerciseCount
import com.example.workouttrackerapp.feature_workout.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow

class GetRoutinesWithExerciseCount(
    private val repository: RoutineRepository
) {
    operator fun invoke(): Flow<List<RoutineWithExerciseCount>> {
        return repository.getRoutinesWithExerciseCount()
    }
}