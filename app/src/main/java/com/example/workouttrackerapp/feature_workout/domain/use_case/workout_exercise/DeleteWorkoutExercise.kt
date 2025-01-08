package com.example.workouttrackerapp.feature_workout.domain.use_case.workout_exercise

import com.example.workouttrackerapp.feature_workout.domain.model.WorkoutExercise
import com.example.workouttrackerapp.feature_workout.domain.repository.WorkoutExerciseRepository

class DeleteWorkoutExercise(
    private val repository: WorkoutExerciseRepository
) {
    suspend operator fun invoke(workoutExercise: WorkoutExercise) {
        return repository.deleteExercise(workoutExercise)
    }
}