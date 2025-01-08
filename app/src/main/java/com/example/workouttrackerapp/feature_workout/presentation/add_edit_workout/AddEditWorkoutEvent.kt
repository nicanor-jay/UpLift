package com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout

import androidx.compose.ui.focus.FocusState
import com.example.workouttrackerapp.feature_workout.domain.model.Record
import com.example.workouttrackerapp.feature_workout.domain.model.WorkoutExercise
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components.RecordDetails

sealed class AddEditWorkoutEvent {
    data class EnteredNote(val value: String) : AddEditWorkoutEvent()
    data class ChangeNoteFocus(val focusState: FocusState) : AddEditWorkoutEvent()
    data class EnteredTimestamp(val value: Long) : AddEditWorkoutEvent()
    data class ChangeColor(val color: Int) : AddEditWorkoutEvent()
    data class EnteredSearch(val value: String) : AddEditWorkoutEvent()
    data class AddExerciseToWorkout(val value: Int) : AddEditWorkoutEvent()
    data class AddSetToExercise(val workoutExerciseId: Int, val prevRecord: Record?) :
        AddEditWorkoutEvent()

    data class ToggleCardExpand(val value: Int) : AddEditWorkoutEvent()
    data class DeleteWorkoutExercise(val value: WorkoutExercise) : AddEditWorkoutEvent()
    data class ToggleEditSetModal(val value: Record?) : AddEditWorkoutEvent()
    data class UpdateRecordDetails(val value: RecordDetails) : AddEditWorkoutEvent()
    data class ToggleExerciseOptionsModal(val value: Int?) : AddEditWorkoutEvent()
    data class SaveExerciseNote(val value: String?) : AddEditWorkoutEvent()
    data class AddRoutineExercisesToWorkout(val routineId: Int) : AddEditWorkoutEvent()
    data class ChangeExercise(val value: Int) : AddEditWorkoutEvent()
    data object ToggleEditNoteModal : AddEditWorkoutEvent()

    data object UpdateRecord : AddEditWorkoutEvent()
    data object SaveWorkout : AddEditWorkoutEvent()
    data object ToggleDatePicker : AddEditWorkoutEvent()
    data object ToggleAddExerciseModal : AddEditWorkoutEvent()
    data object ToggleImportRoutineModal : AddEditWorkoutEvent()
    data object DeleteRecord : AddEditWorkoutEvent()
    data object ToggleOverviewOptionsModal : AddEditWorkoutEvent()
    data object DeleteWorkout : AddEditWorkoutEvent()
    data object ToggleDeleteConfirmationModal : AddEditWorkoutEvent()
    data object ToggleBackHandlerWorkoutDeleteModal : AddEditWorkoutEvent()
    data object ToggleChangeExerciseModal : AddEditWorkoutEvent()


}
