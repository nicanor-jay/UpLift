package com.example.workouttrackerapp.feature_workout.presentation.workouts

import com.example.workouttrackerapp.feature_workout.domain.model.helper.WorkoutWithExerciseCount
import com.example.workouttrackerapp.feature_workout.domain.util.OrderType
import com.example.workouttrackerapp.feature_workout.domain.util.WorkoutOrder

data class WorkoutsState(
    val workouts: List<WorkoutWithExerciseCount> = emptyList(),
    val workoutOrder: WorkoutOrder = WorkoutOrder.Date(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false
)
