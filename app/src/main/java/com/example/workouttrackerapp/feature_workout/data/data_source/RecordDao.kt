package com.example.workouttrackerapp.feature_workout.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.workouttrackerapp.feature_workout.domain.model.Record
import com.example.workouttrackerapp.feature_workout.domain.model.helper.ProcessedRecordsWithTimestamp
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Query(
        """
    SELECT Record.*
    FROM Record
    JOIN WorkoutExercise ON Record.workoutExerciseId = WorkoutExercise.id
    JOIN Workout ON WorkoutExercise.workoutId = Workout.id
    WHERE Workout.id = :id
"""
    )
    fun getWorkoutRecords(id: Int): Flow<List<Record>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: Record)

    @Update
    suspend fun updateRecord(record: Record)

    @Delete
    suspend fun deleteRecord(record: Record)

    @Query(
        """
        SELECT 
            workout.timestamp AS date, 
            strftime('%d/%m/%Y', workout.timestamp / 1000, 'unixepoch') AS formattedDate,
            ROUND(AVG(exercise_stats.avgWeight), 2) AS avgWeight,
            ROUND(AVG(exercise_stats.avgReps), 2) AS avgReps,
            ROUND((AVG(exercise_stats.avgWeight) * AVG(exercise_stats.avgReps)), 2) AS avgIntensity,
            workout.id AS workoutId,
            workout.color AS workoutColor,
            :id AS exerciseId 
        FROM 
            (SELECT 
                WorkoutExercise.workoutId,
                AVG(record.weight) AS avgWeight,
                AVG(record.rep) AS avgReps
             FROM 
                record
             JOIN 
                WorkoutExercise ON WorkoutExercise.id = record.workoutExerciseId
             WHERE 
                WorkoutExercise.exerciseId = :id
                AND record.weight IS NOT NULL  -- Exclude records with null weight
                AND record.isWarmup = 0 -- Exclude warmup records. Could make into option?
             GROUP BY 
                WorkoutExercise.workoutId
            ) AS exercise_stats
        JOIN 
            workout ON workout.id = exercise_stats.workoutId
        GROUP BY 
            date
        ORDER BY 
            date ASC;
        """
    )
    fun getProcessedRecordsWithTimestampByExerciseId(id: Int): Flow<List<ProcessedRecordsWithTimestamp>>

    @Query(
        """
    SELECT workoutId
    FROM WorkoutExercise
    JOIN workout ON workout.id = WorkoutExercise.workoutId
    WHERE WorkoutExercise.exerciseId = :exerciseId
      AND workout.timestamp < :currentTimestamp
    ORDER BY workout.timestamp DESC
    LIMIT 1
    """
    )
    suspend fun getMostRecentPreviousWorkoutIdForExercise(
        exerciseId: Int,
        currentTimestamp: Long
    ): Int?


    @Query(
        """
    SELECT * 
    FROM record 
    WHERE workoutExerciseId IN (
        SELECT id 
        FROM WorkoutExercise 
        WHERE workoutId = :workoutId 
        AND exerciseId = :exerciseId
    )
    """
    )
    suspend fun getRecordsForLatestWorkoutExercise(workoutId: Int, exerciseId: Int): List<Record>

}