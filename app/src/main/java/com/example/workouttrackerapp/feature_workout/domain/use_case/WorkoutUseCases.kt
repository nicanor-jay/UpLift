package com.example.workouttrackerapp.feature_workout.domain.use_case

import com.example.workouttrackerapp.feature_workout.domain.use_case.workout.AddWorkout
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout.DeleteWorkout
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout.GetProcessedWorkouts
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout.GetWidgetData
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout.GetWorkout
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout.GetWorkouts

data class WorkoutUseCases(
    val getWorkouts: GetWorkouts,
    val deleteWorkout: DeleteWorkout,
    val addWorkout: AddWorkout,
    val getWorkout: GetWorkout,
    val getProcessedWorkouts: GetProcessedWorkouts,
    val getWidgetData: GetWidgetData
)