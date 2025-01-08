package com.example.workouttrackerapp.feature_workout.presentation.routine.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.domain.model.helper.RoutineWithExerciseCount

@Composable
fun RoutineItem(
    modifier: Modifier = Modifier,
    routine: RoutineWithExerciseCount,
    onClick: (Int) -> Unit,
    deletable: Boolean = true,
    deleteRoutine: (Int) -> Unit
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .clickable {
            onClick(routine.routineId)
        }
        .padding(
            horizontal = dimensionResource(R.dimen.padding_medium),
            vertical = dimensionResource(R.dimen.padding_extra_small)
        )
    ) {
        Row(
            modifier = modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    routine.routineName,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    routine.exerciseCount.toString() + " exercises",
                    style = MaterialTheme.typography.bodyMedium

                )
            }
            if (deletable) {
                IconButton(
                    onClick = { deleteRoutine(routine.routineId) },
//                            modifier = modifier.weight(1f)
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
}