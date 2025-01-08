package com.example.workouttrackerapp.feature_workout.domain.use_case

import com.example.workouttrackerapp.feature_workout.domain.use_case.exercise.GetExercises
import com.example.workouttrackerapp.feature_workout.domain.use_case.exercise.GetParticipatedExercises

data class ExerciseUseCases(
    val getExercises: GetExercises,
    val getParticipatedExercises: GetParticipatedExercises
)