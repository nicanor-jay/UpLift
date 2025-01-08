package com.example.workouttrackerapp.feature_workout.data.data_source

import androidx.room.Dao
import androidx.room.Query
import com.example.workouttrackerapp.feature_workout.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise_catalog")
    fun getExercises(): Flow<List<Exercise>>

    @Query(
        """
        SELECT DISTINCT e.*
        FROM exercise_catalog e
        JOIN WorkoutExercise we ON e.id = we.exerciseId
        ORDER BY e.name;
    """
    )
    fun getParticipatedExercises(): Flow<List<Exercise>>
}