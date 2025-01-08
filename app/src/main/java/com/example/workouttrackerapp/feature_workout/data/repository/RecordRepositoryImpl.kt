package com.example.workouttrackerapp.feature_workout.data.repository

import com.example.workouttrackerapp.feature_workout.data.data_source.RecordDao
import com.example.workouttrackerapp.feature_workout.domain.model.Record
import com.example.workouttrackerapp.feature_workout.domain.model.helper.ProcessedRecordsWithTimestamp
import com.example.workouttrackerapp.feature_workout.domain.repository.RecordRepository
import kotlinx.coroutines.flow.Flow

class RecordRepositoryImpl(
    private val dao: RecordDao
) : RecordRepository {

    override fun getRecordsByWorkoutId(id: Int): Flow<List<Record>> {
        return dao.getWorkoutRecords(id)
    }

    override fun getProcessedRecordsWithTimestampByExerciseId(id: Int): Flow<List<ProcessedRecordsWithTimestamp>> {
        return dao.getProcessedRecordsWithTimestampByExerciseId(id)
    }

    override suspend fun insertRecord(record: Record) {
        return dao.insertRecord(record)
    }

    override suspend fun updateRecord(record: Record) {
        return dao.updateRecord(record)
    }

    override suspend fun deleteRecord(record: Record) {
        return dao.deleteRecord(record)
    }

    override suspend fun getLatestExerciseRecords(exerciseId: Int, timeStamp: Long): List<Record> {
        // Step 1: Find the most recent workoutId for the given exercise
        val workoutId = dao.getMostRecentPreviousWorkoutIdForExercise(exerciseId, timeStamp)

        // Step 2: If a workout session was found, fetch all related records
        return workoutId?.let { id ->
            dao.getRecordsForLatestWorkoutExercise(id, exerciseId)
        } ?: emptyList() // Return an empty list if no previous workout session exists
    }
}