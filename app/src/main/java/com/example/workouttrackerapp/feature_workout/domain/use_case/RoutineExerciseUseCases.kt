package com.example.workouttrackerapp.feature_workout.domain.use_case

import com.example.workouttrackerapp.feature_workout.domain.use_case.routine_exercise.AddRoutineExercise
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine_exercise.DeleteRoutineExercise
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine_exercise.GetRoutineExercises
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine_exercise.GetRoutineExercisesFlow

data class RoutineExerciseUseCases(
    val addRoutineExercise: AddRoutineExercise,
    val deleteRoutineExercise: DeleteRoutineExercise,
    val getRoutineExercisesFlow: GetRoutineExercisesFlow,
    val getRoutineExercises: GetRoutineExercises
)