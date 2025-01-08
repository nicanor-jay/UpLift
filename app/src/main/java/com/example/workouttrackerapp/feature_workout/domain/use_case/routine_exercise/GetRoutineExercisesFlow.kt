package com.example.workouttrackerapp.feature_workout.domain.use_case.routine_exercise

import com.example.workouttrackerapp.feature_workout.domain.model.RoutineExercise
import com.example.workouttrackerapp.feature_workout.domain.repository.RoutineExerciseRepository
import kotlinx.coroutines.flow.Flow

class GetRoutineExercisesFlow(
    private val repository: RoutineExerciseRepository
) {
    suspend operator fun invoke(routineId: Int): Flow<List<RoutineExercise>> {
        return repository.getRoutineExercisesFlow(routineId)
    }
}