package com.example.workouttrackerapp.feature_workout.data.repository

import com.example.workouttrackerapp.feature_workout.data.data_source.WorkoutExerciseDao
import com.example.workouttrackerapp.feature_workout.domain.model.WorkoutExercise
import com.example.workouttrackerapp.feature_workout.domain.repository.WorkoutExerciseRepository
import kotlinx.coroutines.flow.Flow

class WorkoutExerciseRepositoryImpl(
    private val dao: WorkoutExerciseDao
) : WorkoutExerciseRepository {
    override fun getWorkoutExerciseByWorkoutId(workoutId: Int): Flow<List<WorkoutExercise>> {
        return dao.getWorkoutExerciseByWorkoutId(workoutId)
    }

    override suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long {
        return dao.insertWorkoutExercise(workoutExercise)
    }

    override suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise) {
        return dao.updateWorkoutExercise(workoutExercise)
    }

    override suspend fun updateWorkoutExerciseNote(id: Int, note: String?) {
        return dao.updateWorkoutExerciseNote(id, note)
    }

    override suspend fun deleteExercise(workoutExercise: WorkoutExercise) {
        return dao.deleteWorkoutExercise(workoutExercise)
    }
}