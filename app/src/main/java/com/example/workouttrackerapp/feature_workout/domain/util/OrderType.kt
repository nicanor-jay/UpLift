package com.example.workouttrackerapp.feature_workout.domain.util

sealed class OrderType {
    object Ascending : OrderType()
    object Descending : OrderType()
}
