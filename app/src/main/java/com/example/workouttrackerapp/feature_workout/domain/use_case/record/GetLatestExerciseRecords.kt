package com.example.workouttrackerapp.feature_workout.domain.use_case.record

import com.example.workouttrackerapp.feature_workout.domain.model.Record
import com.example.workouttrackerapp.feature_workout.domain.repository.RecordRepository

class GetLatestExerciseRecords(
    private val repository: RecordRepository
) {
    suspend operator fun invoke(exerciseId: Int, timeStamp: Long): List<Record> {
        return repository.getLatestExerciseRecords(exerciseId, timeStamp)
    }
}