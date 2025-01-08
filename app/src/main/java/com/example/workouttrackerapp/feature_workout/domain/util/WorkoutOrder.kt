package com.example.workouttrackerapp.feature_workout.domain.util

sealed class WorkoutOrder(val orderType: OrderType) {
    class Date(orderType: OrderType) : WorkoutOrder(orderType)
}
