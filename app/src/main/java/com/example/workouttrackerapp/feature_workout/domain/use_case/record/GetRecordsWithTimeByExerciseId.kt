package com.example.workouttrackerapp.feature_workout.domain.use_case.record

import com.example.workouttrackerapp.feature_workout.domain.model.helper.ProcessedRecordsWithTimestamp
import com.example.workouttrackerapp.feature_workout.domain.repository.RecordRepository
import kotlinx.coroutines.flow.Flow

class GetRecordsWithTimeByExerciseId(
    private val repository: RecordRepository
) {
    operator fun invoke(id: Int): Flow<List<ProcessedRecordsWithTimestamp>> {
        return repository.getProcessedRecordsWithTimestampByExerciseId(id)
    }
}