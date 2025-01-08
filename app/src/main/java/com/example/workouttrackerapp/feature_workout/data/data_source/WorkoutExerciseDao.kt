package com.example.workouttrackerapp.feature_workout.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.workouttrackerapp.feature_workout.domain.model.WorkoutExercise
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutExerciseDao {
    @Query("SELECT * FROM workoutexercise WHERE workoutId = :workoutId")
    fun getWorkoutExerciseByWorkoutId(workoutId: Int): Flow<List<WorkoutExercise>>

    @Insert
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long

    @Update
    suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise)

    @Query("UPDATE workoutexercise SET note = :note WHERE id = :id ")
    suspend fun updateWorkoutExerciseNote(id: Int, note: String?)

    @Delete
    suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExercise)
}