package com.example.workouttrackerapp.feature_workout.domain.use_case.record

import com.example.workouttrackerapp.feature_workout.domain.model.Record
import com.example.workouttrackerapp.feature_workout.domain.repository.RecordRepository

class DeleteRecord(private val repository: RecordRepository) {
    suspend operator fun invoke(record: Record) {
        return repository.deleteRecord(record)
    }

}