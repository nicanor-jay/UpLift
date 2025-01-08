package com.example.workouttrackerapp.di

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.workouttrackerapp.di.AppModule.DataStoreManager.Companion.DATASTORE_NAME
import com.example.workouttrackerapp.feature_workout.data.data_source.WorkoutDatabase
import com.example.workouttrackerapp.feature_workout.data.repository.ExerciseRepositoryImpl
import com.example.workouttrackerapp.feature_workout.data.repository.RecordRepositoryImpl
import com.example.workouttrackerapp.feature_workout.data.repository.RoutineExerciseRepositoryImpl
import com.example.workouttrackerapp.feature_workout.data.repository.RoutineRepositoryImpl
import com.example.workouttrackerapp.feature_workout.data.repository.WorkoutExerciseRepositoryImpl
import com.example.workouttrackerapp.feature_workout.data.repository.WorkoutRepositoryImpl
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WeekStartDay
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WeightUnit
import com.example.workouttrackerapp.feature_workout.domain.repository.ExerciseRepository
import com.example.workouttrackerapp.feature_workout.domain.repository.RecordRepository
import com.example.workouttrackerapp.feature_workout.domain.repository.RoutineExerciseRepository
import com.example.workouttrackerapp.feature_workout.domain.repository.RoutineRepository
import com.example.workouttrackerapp.feature_workout.domain.repository.WorkoutExerciseRepository
import com.example.workouttrackerapp.feature_workout.domain.repository.WorkoutRepository
import com.example.workouttrackerapp.feature_workout.domain.use_case.ExerciseUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.RecordUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.RoutineExerciseUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.RoutineUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.WorkoutExerciseUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.WorkoutUseCases
import com.example.workouttrackerapp.feature_workout.domain.use_case.exercise.GetExercises
import com.example.workouttrackerapp.feature_workout.domain.use_case.exercise.GetParticipatedExercises
import com.example.workouttrackerapp.feature_workout.domain.use_case.record.AddRecord
import com.example.workouttrackerapp.feature_workout.domain.use_case.record.DeleteRecord
import com.example.workouttrackerapp.feature_workout.domain.use_case.record.GetLatestExerciseRecords
import com.example.workouttrackerapp.feature_workout.domain.use_case.record.GetRecordsByWorkoutId
import com.example.workouttrackerapp.feature_workout.domain.use_case.record.GetRecordsWithTimeByExerciseId
import com.example.workouttrackerapp.feature_workout.domain.use_case.record.UpdateRecord
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine.AddRoutine
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine.DeleteRoutine
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine.GetRoutine
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine.GetRoutines
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine.GetRoutinesWithExerciseCount
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine.UpdateRoutine
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine_exercise.AddRoutineExercise
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine_exercise.DeleteRoutineExercise
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine_exercise.GetRoutineExercises
import com.example.workouttrackerapp.feature_workout.domain.use_case.routine_exercise.GetRoutineExercisesFlow
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout.AddWorkout
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout.DeleteWorkout
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout.GetProcessedWorkouts
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout.GetWidgetData
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout.GetWorkout
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout.GetWorkouts
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout_exercise.AddWorkoutExercise
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout_exercise.DeleteWorkoutExercise
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout_exercise.GetWorkoutExercises
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout_exercise.UpdateWorkoutExercise
import com.example.workouttrackerapp.feature_workout.domain.use_case.workout_exercise.UpdateWorkoutExerciseNote
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

    @Singleton
    class DataStoreManager @Inject constructor(@ApplicationContext appContext: Context) {

        companion object {
            const val DATASTORE_NAME = "user_datastore"
            val WEEKLY_WORKOUT_TARGET = intPreferencesKey("WEEKLY_WORKOUT_TARGET")
            const val DEFAULT_WEEKLY_WORKOUT_TARGET = 1
            val WEIGHT_UNIT = intPreferencesKey("WEIGHT_UNIT")
            const val DEFAULT_WEIGHT_UNIT_PREF = WeightUnit.DEFAULT_VALUE
            val DYNAMIC_COLORS = booleanPreferencesKey("DYNAMIC_COLORS")
            const val DEFAULT_DYNAMIC_COLORS = false
            val WEEK_START_DAY = intPreferencesKey("WEEK_START_DAY")
            const val DEFAULT_WEEK_START_DAY = WeekStartDay.DEFAULT_VALUE
            val PRIMARY_DYNAMIC_COLOR = intPreferencesKey("PRIMARY_DYNAMIC_COLOR")
        }

        private val settingsDataStore = appContext.dataStore

        suspend fun setWeeklyWorkoutTarget(value: Int) {
            settingsDataStore.edit { settings ->
                settings[WEEKLY_WORKOUT_TARGET] = value
            }
        }

        suspend fun setWeekStartDay(value: Int) {
            settingsDataStore.edit { settings ->
                settings[WEEK_START_DAY] = value
            }
        }

        suspend fun setWeightUnitPref(value: Int) {
            settingsDataStore.edit { settings ->
                settings[WEIGHT_UNIT] = value
            }
        }

        suspend fun toggleDynamicColors(value: Boolean) {
            settingsDataStore.edit { settings ->
                settings[DYNAMIC_COLORS] = value
            }
        }

        suspend fun setPrimaryDynamicColor(value: Int) {
            settingsDataStore.edit { settings ->
                settings[PRIMARY_DYNAMIC_COLOR] = value
            }
        }

        val weeklyWorkoutTarget: Flow<Int> = settingsDataStore.data.map { preferences ->
            preferences[WEEKLY_WORKOUT_TARGET] ?: DEFAULT_WEEKLY_WORKOUT_TARGET
        }

        val weekStartDay: Flow<Int> = settingsDataStore.data.map { preferences ->
            preferences[WEEK_START_DAY] ?: DEFAULT_WEEK_START_DAY
        }

        val weightUnitPref: Flow<Int> = settingsDataStore.data.map { preferences ->
            preferences[WEIGHT_UNIT] ?: DEFAULT_WEIGHT_UNIT_PREF
        }

        val dynamicColors: Flow<Boolean> = settingsDataStore.data.map { preferences ->
            preferences[DYNAMIC_COLORS] ?: DEFAULT_DYNAMIC_COLORS
        }

        val primaryDynamicColor: Flow<Int> = settingsDataStore.data.map { preferences ->
            preferences[PRIMARY_DYNAMIC_COLOR] ?: -10071900
        }

    }

    @Provides
    @Singleton
    fun provideWorkoutDatabase(app: Application): WorkoutDatabase {
        return Room.databaseBuilder(
            app,
            WorkoutDatabase::class.java,
            WorkoutDatabase.DATABASE_NAME
        )
            .createFromAsset("database/myapp.db")
            .build()
    }

    @Provides
    @Singleton
    fun provideWorkoutRepository(db: WorkoutDatabase): WorkoutRepository {
        return WorkoutRepositoryImpl(db.workoutDao)
    }

    @Provides
    @Singleton
    fun provideWorkoutUseCases(repository: WorkoutRepository): WorkoutUseCases {
        return WorkoutUseCases(
            getWorkouts = GetWorkouts(repository),
            deleteWorkout = DeleteWorkout(repository),
            addWorkout = AddWorkout(repository),
            getWorkout = GetWorkout(repository),
            getProcessedWorkouts = GetProcessedWorkouts(repository),
            getWidgetData = GetWidgetData(repository)
        )
    }

    @Provides
    @Singleton
    fun provideExerciseRepository(db: WorkoutDatabase): ExerciseRepository {
        return ExerciseRepositoryImpl(db.exerciseDao)
    }

    @Provides
    @Singleton
    fun provideExerciseUseCases(repository: ExerciseRepository): ExerciseUseCases {
        return ExerciseUseCases(
            getExercises = GetExercises(repository),
            getParticipatedExercises = GetParticipatedExercises(repository),
        )
    }

    @Provides
    @Singleton
    fun provideRecordRepository(db: WorkoutDatabase): RecordRepository {
        return RecordRepositoryImpl(db.recordDao)
    }

    @Provides
    @Singleton
    fun provideRecordUseCases(repository: RecordRepository): RecordUseCases {
        return RecordUseCases(
            getRecordsByWorkoutId = GetRecordsByWorkoutId(repository),
            addRecord = AddRecord(repository),
            updateRecord = UpdateRecord(repository),
            deleteRecord = DeleteRecord(repository),
            getRecordsWithTimeByExerciseId = GetRecordsWithTimeByExerciseId(repository),
            getLatestExerciseRecords = GetLatestExerciseRecords(repository)
        )
    }

    @Provides
    @Singleton
    fun provideWorkoutExerciseRepository(db: WorkoutDatabase): WorkoutExerciseRepository {
        return WorkoutExerciseRepositoryImpl(db.workoutExerciseDao)
    }

    @Provides
    @Singleton
    fun provideWorkoutExerciseUseCases(repository: WorkoutExerciseRepository): WorkoutExerciseUseCases {
        return WorkoutExerciseUseCases(
            addWorkoutExercise = AddWorkoutExercise(repository),
            updateWorkoutExercise = UpdateWorkoutExercise(repository),
            getWorkoutExercises = GetWorkoutExercises(repository),
            updateWorkoutExerciseNote = UpdateWorkoutExerciseNote(repository),
            deleteWorkoutExercise = DeleteWorkoutExercise(repository)
        )
    }

    @Provides
    @Singleton
    fun provideRoutineRepository(db: WorkoutDatabase): RoutineRepository {
        return RoutineRepositoryImpl(db.routineDao)
    }

    @Provides
    @Singleton
    fun provideRoutineUseCases(repository: RoutineRepository): RoutineUseCases {
        return RoutineUseCases(
            getRoutines = GetRoutines(repository),
            getRoutine = GetRoutine(repository),
            getRoutinesWithExerciseCount = GetRoutinesWithExerciseCount(repository),
            addRoutine = AddRoutine(repository),
            deleteRoutine = DeleteRoutine(repository),
            updateRoutine = UpdateRoutine(repository)
        )
    }

    @Provides
    @Singleton
    fun provideRoutineWorkoutRepository(db: WorkoutDatabase): RoutineExerciseRepository {
        return RoutineExerciseRepositoryImpl(db.routineExerciseDao)
    }

    @Provides
    @Singleton
    fun provideRoutineExerciseUseCases(repository: RoutineExerciseRepository): RoutineExerciseUseCases {
        return RoutineExerciseUseCases(
            addRoutineExercise = AddRoutineExercise(repository),
            deleteRoutineExercise = DeleteRoutineExercise(repository),
            getRoutineExercisesFlow = GetRoutineExercisesFlow(repository),
            getRoutineExercises = GetRoutineExercises(repository)
        )
    }
}
