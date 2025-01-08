package com.example.workouttrackerapp.feature_workout.domain.use_case.workout_exercise

import com.example.workouttrackerapp.feature_workout.domain.repository.WorkoutExerciseRepository

class UpdateWorkoutExerciseNote(
    private val repository: WorkoutExerciseRepository
) {
    suspend operator fun invoke(id: Int, note: String?) {
        return repository.updateWorkoutExerciseNote(id, note)
    }
}