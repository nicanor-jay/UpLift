package com.example.workouttrackerapp.feature_workout.presentation.routine.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.domain.model.Exercise
import com.example.workouttrackerapp.feature_workout.domain.model.RoutineExercise

@Composable
fun RoutineExerciseItem(
    modifier: Modifier = Modifier,
    index: Int?,
    routineExercise: RoutineExercise?,
    exercise: Exercise,
    onClick: (Int) -> Unit,
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.padding_extra_small))
    ) {
        Row(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = modifier.weight(5f)) {
                Text(
                    exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    exercise.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            IconButton(
                onClick = { onClick(routineExercise?.id ?: index!!) },
                modifier = modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete workout",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}