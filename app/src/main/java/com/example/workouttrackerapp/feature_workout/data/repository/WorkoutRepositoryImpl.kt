package com.example.workouttrackerapp.feature_workout.data.repository

import com.example.workouttrackerapp.feature_workout.data.data_source.WorkoutDao
import com.example.workouttrackerapp.feature_workout.domain.model.Workout
import com.example.workouttrackerapp.feature_workout.domain.model.helper.ProcessedWorkoutsWithTimestamp
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WorkoutWithExerciseCount
import com.example.workouttrackerapp.feature_workout.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow

class WorkoutRepositoryImpl(
    private val dao: WorkoutDao
) : WorkoutRepository {

    override fun getWorkouts(): Flow<List<WorkoutWithExerciseCount>> {
        return dao.getWorkouts()
    }

    override fun getProcessedWorkouts(): Flow<List<ProcessedWorkoutsWithTimestamp>> {
        return dao.getProcessedWorkouts()
    }

    override fun getWidgetData(): Flow<List<Boolean>> {
        return dao.getWidgetData()
    }

    override suspend fun getWorkoutById(id: Int): Workout? {
        return dao.getWorkoutById(id)
    }

    override suspend fun insertWorkout(workout: Workout): Long {
        return dao.insertWorkout(workout)
    }

    override suspend fun upsertWorkout(workout: Workout): Long? {
        return dao.upsertWorkout(workout)
    }

    override suspend fun updateWorkout(workout: Workout) {
        return dao.updateWorkout(workout)
    }

    override suspend fun deleteWorkout(workout: Workout) {
        return dao.deleteWorkout(workout)
    }


}