package com.example.workouttrackerapp.feature_workout.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.workouttrackerapp.ui.theme.Blue
import com.example.workouttrackerapp.ui.theme.Green
import com.example.workouttrackerapp.ui.theme.Orange
import com.example.workouttrackerapp.ui.theme.Pink
import com.example.workouttrackerapp.ui.theme.Purple
import com.example.workouttrackerapp.ui.theme.Red
import com.example.workouttrackerapp.ui.theme.Yellow

@Entity
data class Workout(
    @PrimaryKey val id: Int? = null,
    val timestamp: Long,
    val color: Int,
    val note: String?
) {
    companion object {
        val workoutColors = listOf(Red, Blue, Green, Yellow, Orange, Pink, Purple)
    }
}
