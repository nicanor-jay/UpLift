package com.example.workouttrackerapp.feature_workout.data.data_source

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.workouttrackerapp.feature_workout.domain.model.Workout
import com.example.workouttrackerapp.feature_workout.domain.model.helper.ProcessedWorkoutsWithTimestamp
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WorkoutWithExerciseCount
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query(
        """
        SELECT w.id AS workoutId, w.timestamp, w.color, w.note, COUNT(we.id) AS exerciseCount
        FROM Workout w
        LEFT JOIN WorkoutExercise we ON w.id = we.workoutId
        GROUP BY w.id
        ORDER BY w.timestamp DESC
    """
    )
    fun getWorkouts(): Flow<List<WorkoutWithExerciseCount>>

    @Query(
        """
    SELECT 
        w.id AS workoutId,
        w.timestamp AS date,
        STRFTIME('%Y/%m/%d', w.timestamp / 1000, 'unixepoch') AS formattedDate,
        w.color AS workoutColor,
        COUNT(we.id) AS numExercises
    FROM 
        Workout w
    LEFT JOIN 
        WorkoutExercise we ON w.id = we.workoutId
    GROUP BY 
        w.id
    ORDER BY 
        w.timestamp DESC
    """
    )
    fun getProcessedWorkouts(): Flow<List<ProcessedWorkoutsWithTimestamp>>


    @Query("""
    WITH RECURSIVE DateRange AS (
        SELECT DATE('now', '-69 day') AS date
        UNION ALL
        SELECT DATE(date, '+1 day')
        FROM DateRange
        WHERE date < DATE('now')
    )
    SELECT 
        CASE 
            WHEN EXISTS (
                SELECT 1 
                FROM Workout 
                WHERE DATE(timestamp / 1000, 'unixepoch') = DateRange.date
                AND EXISTS (
                    SELECT 1
                    FROM WorkoutExercise
                    WHERE WorkoutExercise.workoutId = Workout.id
                )
            ) THEN 1
            ELSE 0
        END AS is_workout_present
    FROM DateRange;
""")
    fun getWidgetData(): Flow<List<Boolean>>



    @Query("SELECT * FROM workout WHERE id = :id")
    suspend fun getWorkoutById(id: Int): Workout?

    @Insert
    suspend fun insertWorkout(workout: Workout): Long

    @Update
    suspend fun updateWorkout(workout: Workout)

    suspend fun upsertWorkout(workout: Workout): Long? {
        try {
            return insertWorkout(workout)
        } catch (e: SQLiteConstraintException) {
            updateWorkout(workout)
            return null
        }
    }

    @Delete
    suspend fun deleteWorkout(workout: Workout)
}