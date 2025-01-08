package com.example.workouttrackerapp.feature_workout.domain.use_case.workout

import com.example.workouttrackerapp.feature_workout.domain.model.Workout
import com.example.workouttrackerapp.feature_workout.domain.repository.WorkoutRepository

class GetWorkout(
    private val repository: WorkoutRepository
) {

    suspend operator fun invoke(id: Int): Workout? {
        return repository.getWorkoutById(id)
    }
}