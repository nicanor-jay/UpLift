package com.example.workouttrackerapp.feature_workout.data.repository

import com.example.workouttrackerapp.feature_workout.data.data_source.ExerciseDao
import com.example.workouttrackerapp.feature_workout.domain.model.Exercise
import com.example.workouttrackerapp.feature_workout.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow

class ExerciseRepositoryImpl(
    private val dao: ExerciseDao
) : ExerciseRepository {

    override fun getExercises(): Flow<List<Exercise>> {
        return dao.getExercises()
    }

    override fun getParticipatedExercises(): Flow<List<Exercise>> {
        return dao.getParticipatedExercises()
    }
}