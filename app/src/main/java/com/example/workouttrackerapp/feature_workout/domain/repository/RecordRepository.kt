package com.example.workouttrackerapp.feature_workout.domain.repository

import com.example.workouttrackerapp.feature_workout.domain.model.Record
import com.example.workouttrackerapp.feature_workout.domain.model.helper.ProcessedRecordsWithTimestamp
import kotlinx.coroutines.flow.Flow

interface RecordRepository {
    fun getRecordsByWorkoutId(id: Int): Flow<List<Record>>
    fun getProcessedRecordsWithTimestampByExerciseId(id: Int): Flow<List<ProcessedRecordsWithTimestamp>>
    suspend fun insertRecord(record: Record)
    suspend fun updateRecord(record: Record)
    suspend fun deleteRecord(record: Record)
    suspend fun getLatestExerciseRecords(exerciseId: Int, timeStamp: Long): List<Record>
}