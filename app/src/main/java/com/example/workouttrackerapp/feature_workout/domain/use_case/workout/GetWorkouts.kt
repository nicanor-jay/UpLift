package com.example.workouttrackerapp.feature_workout.domain.use_case.workout

import com.example.workouttrackerapp.feature_workout.domain.model.helper.WorkoutWithExerciseCount
import com.example.workouttrackerapp.feature_workout.domain.repository.WorkoutRepository
import com.example.workouttrackerapp.feature_workout.domain.util.OrderType
import com.example.workouttrackerapp.feature_workout.domain.util.WorkoutOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetWorkouts(
    private val repository: WorkoutRepository
) {
    operator fun invoke(
        workoutOrder: WorkoutOrder = WorkoutOrder.Date(OrderType.Descending)
    ): Flow<List<WorkoutWithExerciseCount>> {
        return repository.getWorkouts().map { workouts ->
            when (workoutOrder.orderType) {
                is OrderType.Ascending -> {
                    when (workoutOrder) {
                        is WorkoutOrder.Date -> workouts.sortedBy { it.timestamp }
                    }
                }

                is OrderType.Descending -> {
                    when (workoutOrder) {
                        is WorkoutOrder.Date -> workouts.sortedByDescending { it.timestamp }
                    }
                }
            }
        }
    }
}