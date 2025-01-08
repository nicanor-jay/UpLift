package com.example.workouttrackerapp.feature_workout.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.workouttrackerapp.feature_workout.domain.model.Routine
import com.example.workouttrackerapp.feature_workout.domain.model.helper.RoutineWithExerciseCount
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routine")
    fun getRoutines(): Flow<List<Routine>>

    @Query("SELECT * FROM routine WHERE routine.id = :routineId")
    fun getRoutine(routineId: Int): Routine?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRoutine(routine: Routine): Long

    @Update
    fun updateRoutine(routine: Routine)

    @Query(
        """
            SELECT r.id AS routineId, 
                   r.name AS routineName, 
                   COUNT(re.id) AS exerciseCount
            FROM Routine r
            LEFT JOIN RoutineExercise re ON r.id = re.routineId
            GROUP BY r.id
            ORDER BY r.name ASC
        """
    )
    fun getRoutinesWithExerciseCount(): Flow<List<RoutineWithExerciseCount>>


    @Delete
    fun deleteRoutine(routine: Routine)
}