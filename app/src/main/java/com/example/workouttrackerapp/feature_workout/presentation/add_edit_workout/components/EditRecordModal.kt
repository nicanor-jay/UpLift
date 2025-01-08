package com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.domain.model.Record

@Composable
fun EditRecordModal(
    modifier: Modifier = Modifier,
    record: Record,
    recordDetails: RecordDetails,
    updateUiState: (RecordDetails) -> Unit = {},
    deleteRecord: () -> Unit,
    onDismissRequest: () -> Unit,
    saveRecord: () -> Unit,
    deleteEnabled: Boolean
) {

    val focusRequester = remember { FocusRequester() }

    val weightFieldValue =
        remember {
            mutableStateOf(
                TextFieldValue(
                    recordDetails.weight?.toString() ?: "",
                    TextRange(recordDetails.weight?.toString()?.length ?: 0)
                )
            )
        }

    val repsFieldValue =
        remember {
            mutableStateOf(
                TextFieldValue(
                    recordDetails.rep?.toString() ?: "",
                    TextRange(recordDetails.rep?.toString()?.length ?: 0)
                )
            )
        }

    val isWarmupValue =
        remember {
            mutableStateOf(
                recordDetails.isWarmup
            )
        }

    Dialog(onDismissRequest = onDismissRequest) {
        ElevatedCard(
            modifier = Modifier
//                .padding(dimensionResource(R.dimen.padding_medium))
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
            ) {
                Text(
                    "Edit Set",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = modifier.align(Alignment.CenterHorizontally)
                )

                HorizontalDivider(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.secondary
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    value = weightFieldValue.value,
                    onValueChange = {
                        weightFieldValue.value = it
                    },
                    label = { Text(stringResource(R.string.weight)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.FitnessCenter, stringResource(R.string.weight))
                    }
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_extra_small)))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = repsFieldValue.value,
                    onValueChange = {
                        repsFieldValue.value = it
                    },
                    label = { Text(stringResource(R.string.reps)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Numbers, "Reps")
                    }
                )
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(R.string.mark_as_warmup_set))
                    Switch(isWarmupValue.value, { isWarmupValue.value = it })
                }
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                Button(
                    onClick = {
                        updateUiState(
                            recordDetails.copy(
                                weight = weightFieldValue.value.text.toFloatOrNull(),
                                rep = repsFieldValue.value.text.toIntOrNull(),
                                isWarmup = isWarmupValue.value
                            )
                        )
                        saveRecord()

                        Log.d(
                            "EditRecordModal",
                            "Weight: " + weightFieldValue.value.text.toLongOrNull()
                        )

                    },
                    enabled =
                    repsFieldValue.value.text.isDigitsOnly() && repsFieldValue.value.text.isNotEmpty() && weightFieldValue.value.text.toFloatOrNull() != null,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.save))
                }
                Button(
                    onClick = {
                        deleteRecord()
                        onDismissRequest()
                    },
                    enabled = deleteEnabled,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(
                        "Delete",
                        color = if (deleteEnabled) MaterialTheme.colorScheme.onError else Color.Gray
                    )
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

data class RecordDetails(
    val weight: Float? = null,
    val rep: Int? = null,
    val isWarmup: Boolean = false
)