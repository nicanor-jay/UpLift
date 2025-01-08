package com.example.workouttrackerapp.feature_workout.domain.model.helper

import java.time.DayOfWeek

enum class WeekStartDay(val displayName: String, val value: Int) {
    SATURDAY("Saturday", DayOfWeek.SATURDAY.value),
    SUNDAY("Sunday", DayOfWeek.SUNDAY.value),
    MONDAY("Monday", DayOfWeek.MONDAY.value);

    companion object {
        fun fromDisplayName(displayName: String): WeekStartDay? {
            return entries.find { it.displayName.equals(displayName, ignoreCase = true) }
        }

        fun fromValue(value: Int): WeekStartDay? {
            return entries.find { it.value == value }
        }

        const val DEFAULT_VALUE = 7
    }

}