package com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.domain.model.Record

@Composable
fun PreviousRecordsCard(
    modifier: Modifier = Modifier,
    exerciseName: String,
    weightLabel: String,
    prevRecords: List<Record>
) {
    ElevatedCard(
        modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
    ) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_small))
                .fillMaxWidth()
        ) {
            Text(
                "Previous $exerciseName entry",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dimensionResource(R.dimen.padding_small)),
                textAlign = TextAlign.Center
            )
            RecordsHeading(isBold = false, weightLabel = weightLabel)
            prevRecords.filter { it.isWarmup }.forEachIndexed { index, record ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_extra_small)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Thermostat, "Warm-up set")
                            Text(
                                (index + 1).toString(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            record.weight.toString(),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium

                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            record.rep.toString(),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium

                        )
                    }
                }
            }
            prevRecords.filter { !it.isWarmup }.forEachIndexed { index, record ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_extra_small)),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            (index + 1).toString(),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            record.weight.toString(),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium

                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            record.rep.toString(),
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium

                        )
                    }
                }
            }
        }
    }
}