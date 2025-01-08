package com.example.workouttrackerapp.feature_workout.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.workouttrackerapp.feature_workout.domain.model.Exercise
import com.example.workouttrackerapp.feature_workout.domain.model.Record
import com.example.workouttrackerapp.feature_workout.domain.model.Routine
import com.example.workouttrackerapp.feature_workout.domain.model.RoutineExercise
import com.example.workouttrackerapp.feature_workout.domain.model.Workout
import com.example.workouttrackerapp.feature_workout.domain.model.WorkoutExercise

@Database(
    entities = [Workout::class, Exercise::class, WorkoutExercise::class, Record::class, Routine::class, RoutineExercise::class],
    version = 1,
//    exportSchema = false
)

abstract class WorkoutDatabase : RoomDatabase() {

    abstract val workoutDao: WorkoutDao
    abstract val exerciseDao: ExerciseDao
    abstract val workoutExerciseDao: WorkoutExerciseDao
    abstract val recordDao: RecordDao
    abstract val routineDao: RoutineDao
    abstract val routineExerciseDao: RoutineExerciseDao

    companion object {
        const val DATABASE_NAME = "workouts_db"
    }
}