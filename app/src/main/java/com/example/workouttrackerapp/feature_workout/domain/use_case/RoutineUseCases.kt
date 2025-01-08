package com.example.workouttrackerapp.feature_workout.domain.use_case

import com.example.workouttrackerapp.feature_workout.domain.use_case.routine.AddRoutine
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine.DeleteRoutine
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine.GetRoutine
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine.GetRoutines
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine.GetRoutinesWithExerciseCount
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine.UpdateRoutine

data class RoutineUseCases(
    val getRoutines: GetRoutines,
    val addRoutine: AddRoutine,
    val deleteRoutine: DeleteRoutine,
    val updateRoutine: UpdateRoutine,
    val getRoutine: GetRoutine,
    val getRoutinesWithExerciseCount: GetRoutinesWithExerciseCount
)