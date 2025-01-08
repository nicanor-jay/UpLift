package com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.domain.model.Exercise
import com.example.workouttrackerapp.feature_workout.domain.model.Record
import com.example.workouttrackerapp.feature_workout.domain.model.WorkoutExercise

@Composable
fun WorkoutExerciseItem(
    modifier: Modifier = Modifier,
    workoutExercise: WorkoutExercise,
    exercise: Exercise,
    recordList: List<Record>,
    prevRecordsList: List<Record>,
    toggleExpand: (Int) -> Unit,
    expandedState: Boolean,
    addSet: (Int) -> Unit,
    toggleEditSetModal: (Record?) -> Unit,
    toggleExerciseOptionsModal: (Int) -> Unit,
    weightLabel: String
) {

    Log.d("WorkoutExerciseItem", "Record list size: " + recordList.size.toString())

    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f, label = ""
    )

    ElevatedCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.padding_extra_small))
            .clickable { workoutExercise.id?.let { toggleExpand(it) } }
    ) {
        Column(
            modifier = Modifier.animateContentSize(
                animationSpec = tween(
                    durationMillis = 300, easing = LinearOutSlowInEasing
                )
            )
        ) {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier
                        .weight(6f)
                        .padding(start = dimensionResource(R.dimen.padding_medium)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        overflow = TextOverflow.Clip,
                        maxLines = 1
                    )

                    IconButton({ toggleExerciseOptionsModal(workoutExercise.id!!) }) {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)) {
                            Icon(
                                Icons.Default.MoreHoriz,
                                "Exercise Options",
                                modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.padding_extra_small))
                            )
                        }
                    }
                }

                IconButton(modifier = Modifier
                    .weight(1f)
                    .alpha(0.2f)
                    .rotate(rotationState), onClick = {
                    workoutExercise.id?.let {
                        toggleExpand(it)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }

            if (expandedState) {
                if (workoutExercise.note != null) {
                    Row(
                        modifier
                            .padding(horizontal = dimensionResource(R.dimen.padding_medium)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            workoutExercise.note,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Column(modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_small))) {
                    if (recordList.isNotEmpty()) {
                        RecordsHeading(weightLabel = weightLabel)
                        // Warmup sets first
                        recordList.filter { it.isWarmup }.forEachIndexed { index, record ->
                            RecordItem(
                                setNumber = index,
                                record = record,
                                toggleEditSetModal = { toggleEditSetModal(record) },
                                isFinalSet = index == recordList.lastIndex
                            )
                        }
                        // Working sets after
                        recordList.filter { !it.isWarmup }.forEachIndexed { index, record ->
                            RecordItem(
                                setNumber = index,
                                record = record,
                                toggleEditSetModal = { toggleEditSetModal(record) },
                                isFinalSet = index == recordList.lastIndex
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Button(
                            onClick = {
                                addSet(workoutExercise.id!!)
                            },
                            enabled = if (recordList.isNotEmpty()) {
                                recordList.last().weight != null && recordList.last().rep != null
                            } else {
                                true
                            }
                        ) {
                            Icon(Icons.Default.Add, "Add Set")
                            Text(stringResource(R.string.add_set))
                        }
                    }
                    if (prevRecordsList.any { it.weight != null }) {
                        PreviousRecordsCard(
                            prevRecords = prevRecordsList.filter { it.weight != null },
                            exerciseName = exercise.name,
                            weightLabel = weightLabel
                        )
                    }
                }

            }
        }
    }
}