package com.example.workouttrackerapp.feature_workout.domain.use_case

import com.example.workouttrackerapp.feature_workout.domain.use_case.workout_exercise.AddWorkoutExercise
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout_exercise.DeleteWorkoutExercise
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout_exercise.GetWorkoutExercises
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout_exercise.UpdateWorkoutExercise
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout_exercise.UpdateWorkoutExerciseNote

data class WorkoutExerciseUseCases(
    val addWorkoutExercise: AddWorkoutExercise,
    val updateWorkoutExercise: UpdateWorkoutExercise,
    val getWorkoutExercises: GetWorkoutExercises,
    val updateWorkoutExerciseNote: UpdateWorkoutExerciseNote,
    val deleteWorkoutExercise: DeleteWorkoutExercise
)