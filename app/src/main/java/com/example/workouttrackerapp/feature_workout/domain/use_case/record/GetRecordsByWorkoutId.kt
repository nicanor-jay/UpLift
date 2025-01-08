package com.example.workouttrackerapp.feature_workout.domain.use_case.record

import com.example.workouttrackerapp.feature_workout.domain.model.Record
import com.example.workouttrackerapp.feature_workout.domain.repository.RecordRepository
import kotlinx.coroutines.flow.Flow

class GetRecordsByWorkoutId(
    private val repository: RecordRepository
) {
    operator fun invoke(id: Int): Flow<List<Record>> {
        return repository.getRecordsByWorkoutId(id)
    }
}