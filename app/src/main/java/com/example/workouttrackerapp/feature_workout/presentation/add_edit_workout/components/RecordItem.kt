package com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.domain.model.Record

@Composable
fun RecordItem(
    setNumber: Int,
    record: Record,
    toggleEditSetModal: (Record?) -> Unit,
    isFinalSet: Boolean,
) {

    val (containerColor, textColor) = if (record.isWarmup) {
        MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.background to MaterialTheme.colorScheme.onBackground
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(R.dimen.padding_extra_small))
            .clickable { toggleEditSetModal(record) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimensionResource(R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedCard(
                colors = CardDefaults.cardColors(containerColor = containerColor),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(
                            vertical = dimensionResource(R.dimen.padding_extra_small),
                            horizontal = dimensionResource(R.dimen.padding_small)
                        )
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (record.isWarmup) {
                        Icon(Icons.Default.Thermostat, "Warmup set")
                    }
                    Text(
                        (setNumber + 1).toString(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimensionResource(R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedCard(
                colors = CardDefaults.cardColors(containerColor = containerColor),
            ) {
                Text(
                    record.weight?.toString() ?: "-",
                    modifier = Modifier
                        .padding(
                            vertical = dimensionResource(R.dimen.padding_extra_small),
                            horizontal = dimensionResource(R.dimen.padding_small)
                        )
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    overflow = TextOverflow.Clip,
                    maxLines = 1
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimensionResource(R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ElevatedCard(
                colors = CardDefaults.cardColors(containerColor = containerColor),
            ) {
                Text(
                    record.rep?.toString() ?: "-",
                    modifier = Modifier
                        .padding(
                            vertical = dimensionResource(R.dimen.padding_extra_small),
                            horizontal = dimensionResource(R.dimen.padding_small)
                        )
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    overflow = TextOverflow.Clip,
                    maxLines = 1

                )
            }
        }

    }
}