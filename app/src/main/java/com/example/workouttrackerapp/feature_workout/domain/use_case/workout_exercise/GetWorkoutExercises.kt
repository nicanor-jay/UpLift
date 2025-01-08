package com.example.workouttrackerapp.feature_workout.domain.use_case.workout_exercise

import com.example.workouttrackerapp.feature_workout.domain.model.WorkoutExercise
import com.example.workouttrackerapp.feature_workout.domain.repository.WorkoutExerciseRepository
import kotlinx.coroutines.flow.Flow

class GetWorkoutExercises(
    private val repository: WorkoutExerciseRepository
) {
    operator fun invoke(workoutId: Int): Flow<List<WorkoutExercise>> {
        return repository.getWorkoutExerciseByWorkoutId(workoutId)
    }
}