package com.example.workouttrackerapp.feature_workout.domain.use_case.routine_exercise

import com.example.workouttrackerapp.feature_workout.domain.model.RoutineExercise
import com.example.workouttrackerapp.feature_workout.domain.repository.RoutineExerciseRepository

class GetRoutineExercises(
    private val repository: RoutineExerciseRepository
) {
    operator fun invoke(routineId: Int): List<RoutineExercise> {
        return repository.getRoutineExercises(routineId)
    }
}