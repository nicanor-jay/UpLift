package com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.domain.model.Exercise
import com.example.workouttrackerapp.feature_workout.domain.model.WorkoutExercise

@Composable
fun ExerciseOptionsModal(
    modifier: Modifier = Modifier,
    exercise: Exercise,
    workoutExercise: WorkoutExercise,
    toggleEditNoteModal: () -> Unit,
    toggleChangeExerciseModal: () -> Unit,
    deleteWorkoutExercise: (WorkoutExercise) -> Unit,
    onDismissRequest: () -> Unit
) {

    val noteLabel = if (workoutExercise.note != null) {
        "Edit note"
    } else {
        "Add note"
    }

    Dialog(onDismissRequest = onDismissRequest) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
            ) {
                Text(
                    exercise.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    exercise.description,
                    modifier = modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                HorizontalDivider(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.secondary
                )

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            toggleEditNoteModal()
                        }
                ) {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(8.dp) // Adjust padding as needed
                            .height(40.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.EditNote, "Add/Edit Note")
                        Spacer(modifier = modifier.width(dimensionResource(R.dimen.padding_small)))
                        Text(
                            noteLabel,
                            modifier = modifier
                                .fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold

                        )
                    }
                }

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            toggleChangeExerciseModal()
                        }
                ) {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(8.dp) // Adjust padding as needed
                            .height(40.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.FitnessCenter, "Change Exercise")
                        Spacer(modifier = modifier.width(dimensionResource(R.dimen.padding_small)))
                        Text(
                            "Change exercise",
                            modifier = modifier
                                .fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold

                        )
                    }
                }

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable {
                            deleteWorkoutExercise(workoutExercise)
                        }
                ) {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(8.dp) // Adjust padding as needed
                            .height(40.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Default.Delete, "Remove exercise")
                        Spacer(modifier = modifier.width(dimensionResource(R.dimen.padding_small)))
                        Text(
                            "Remove exercise",
                            modifier = modifier
                                .fillMaxWidth(),
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold

                        )
                    }
                }
            }
        }
    }

}