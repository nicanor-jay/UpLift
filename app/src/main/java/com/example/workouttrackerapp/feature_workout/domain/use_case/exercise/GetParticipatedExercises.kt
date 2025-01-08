package com.example.workouttrackerapp.feature_workout.domain.use_case.exercise

import com.example.workouttrackerapp.feature_workout.domain.model.Exercise
import com.example.workouttrackerapp.feature_workout.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow

class GetParticipatedExercises(
    private val repository: ExerciseRepository
) {
    operator fun invoke(): Flow<List<Exercise>> {
        return repository.getParticipatedExercises()
    }
}