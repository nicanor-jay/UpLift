package com.example.workouttrackerapp.feature_workout.presentation.workouts.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.domain.model.Workout
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WorkoutWithExerciseCount
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WorkoutItem(
    workout: WorkoutWithExerciseCount,
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit
) {
    val date = Date(workout.timestamp)
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.UK)

    Card(
        modifier = modifier
            .fillMaxWidth(),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Box(contentAlignment = BiasAlignment(-.2f, -1f)) {
            Icon(
                imageVector = Icons.Default.Bookmark,
                "Bookmark",
                tint = Workout.workoutColors[workout.color],
                modifier = Modifier.scale(1.5f)
            )

            Row(
                modifier = modifier
                    .padding(5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formatter.format(date),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        workout.exerciseCount.toString() + " exercises",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )

                }

                IconButton(
                    onClick = onDeleteClick,
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_workout),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun WorkoutItemPreview1() {
    WorkoutItem(
        WorkoutWithExerciseCount(
            0,
            1728053849172,
            color = 0,
            "",
            3,
        ),
        modifier = TODO(),
        onDeleteClick = TODO()
    )
}

@Preview
@Composable
fun WorkoutItemPreview2() {
    WorkoutItem(
        WorkoutWithExerciseCount(
            1,
            1728053849172,
            color = 0,
            "",
            4
        ),
        modifier = TODO(),
        onDeleteClick = TODO()
    )
}