package com.example.workouttrackerapp.feature_workout.domain.use_case.routine_exercise

import com.example.workouttrackerapp.feature_workout.domain.model.RoutineExercise
import com.example.workouttrackerapp.feature_workout.domain.repository.RoutineExerciseRepository

class AddRoutineExercise(
    private val repository: RoutineExerciseRepository
) {
    suspend operator fun invoke(routineExercise: RoutineExercise) {
        return repository.addRoutineExercise(routineExercise)
    }
}