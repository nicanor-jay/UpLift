package com.example.workouttrackerapp.feature_workout.presentation.workouts

import com.example.workouttrackerapp.feature_workout.domain.util.WorkoutOrder

sealed class WorkoutsEvent {
    data class Order(val workoutOrder: WorkoutOrder) : WorkoutsEvent()

    //    data class DeleteWorkout(val workout: Workout) : WorkoutsEvent()
    data class ToggleDeleteWorkoutModal(val workoutId: Int?) : WorkoutsEvent()

    //    data class AddWorkout(val workout: Workout): WorkoutsEvent()
    object AddWorkout : WorkoutsEvent()
    object DeleteWorkout : WorkoutsEvent()

    object ToggleOrderSection : WorkoutsEvent()
}