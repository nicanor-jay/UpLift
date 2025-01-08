package com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.domain.model.Exercise

@Composable
fun ExerciseWithDescriptionItem(
    modifier: Modifier = Modifier,
    exercise: Exercise,
    onClick: (Int) -> Unit,
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.padding_extra_small))
            .clickable { exercise.id?.let { onClick(it) } }
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
        ) {
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

    }
}

@Preview
@Composable
fun ExerciseWithDescriptionItemPreview() {
    ExerciseWithDescriptionItem(Modifier, Exercise(1, "Push-ups", "Chest and tricep exercise."), {})
}