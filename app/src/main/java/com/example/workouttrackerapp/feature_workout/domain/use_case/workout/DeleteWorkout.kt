package com.example.workouttrackerapp.feature_workout.domain.use_case.workout

import com.example.workouttrackerapp.feature_workout.domain.model.Workout
import com.example.workouttrackerapp.feature_workout.domain.repository.WorkoutRepository

class DeleteWorkout(
    private val repository: WorkoutRepository
) {
    suspend operator fun invoke(workout: Workout) {
        repository.deleteWorkout(workout)
    }
}