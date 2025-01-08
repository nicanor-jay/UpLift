package com.example.workouttrackerapp.feature_workout.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.workouttrackerapp.feature_workout.domain.model.RoutineExercise
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineExerciseDao {
    @Insert
    suspend fun insertRoutineExercise(routineExercise: RoutineExercise)

    @Delete
    suspend fun deleteRoutineExercise(routineExercise: RoutineExercise)

    @Query(
        """
        SELECT * FROM exercise_catalog e
        JOIN routineexercise re ON e.id = re.exerciseId
        WHERE re.routineId = :routineId
    """
    )
    fun getRoutineExercisesFlow(routineId: Int): Flow<List<RoutineExercise>>

    @Query(
        """
        SELECT * FROM exercise_catalog e
        JOIN routineexercise re ON e.id = re.exerciseId
        WHERE re.routineId = :routineId
    """
    )
    fun getRoutineExercises(routineId: Int): List<RoutineExercise>
}