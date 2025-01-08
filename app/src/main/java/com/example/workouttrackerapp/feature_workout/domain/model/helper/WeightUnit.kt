package com.example.workouttrackerapp.feature_workout.domain.model.helper

enum class WeightUnit(val displayName: String, val value: Int) {
    KG("KG", 0),
    LB("LB", 1);

    companion object {
        fun fromDisplayName(displayName: String): WeightUnit? {
            return WeightUnit.entries.find { it.displayName.equals(displayName, ignoreCase = true) }
        }

        fun fromValue(value: Int): WeightUnit? {
            return WeightUnit.entries.find { it.value == value }
        }

        const val DEFAULT_VALUE = 0
    }
}