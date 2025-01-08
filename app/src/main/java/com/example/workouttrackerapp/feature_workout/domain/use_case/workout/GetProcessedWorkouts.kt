package com.example.workouttrackerapp.feature_workout.domain.use_case.workout

import com.example.workouttrackerapp.feature_workout.domain.model.helper.ProcessedWorkoutsWithTimestamp
import com.example.workouttrackerapp.feature_workout.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow

class GetProcessedWorkouts(
    private val repository: WorkoutRepository
) {
    operator fun invoke(): Flow<List<ProcessedWorkoutsWithTimestamp>> {
        return repository.getProcessedWorkouts()
    }
}