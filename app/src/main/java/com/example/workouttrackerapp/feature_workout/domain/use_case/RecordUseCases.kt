package com.example.workouttrackerapp.feature_workout.domain.use_case

import com.example.workouttrackerapp.feature_workout.domain.use_case.record.AddRecord
import com.example.workouttrackerapp.feature_workout.domain.use_case.record.DeleteRecord
import com.example.workouttrackerapp.feature_workout.domain.use_case.record.GetLatestExerciseRecords
import com.example.workouttrackerapp.feature_workout.domain.use_case.record.GetRecordsByWorkoutId
import com.example.workouttrackerapp.feature_workout.domain.use_case.record.GetRecordsWithTimeByExerciseId
import com.example.workouttrackerapp.feature_workout.domain.use_case.record.UpdateRecord

data class RecordUseCases(
    val getRecordsByWorkoutId: GetRecordsByWorkoutId,
    val addRecord: AddRecord,
    val updateRecord: UpdateRecord,
    val deleteRecord: DeleteRecord,
    val getRecordsWithTimeByExerciseId: GetRecordsWithTimeByExerciseId,
    val getLatestExerciseRecords: GetLatestExerciseRecords
)