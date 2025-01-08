package com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.workouttrackerapp.R

@Composable
fun EditNoteModal(
    modifier: Modifier = Modifier,
    note: String?,
    saveNote: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    val noteFieldValue =
        remember {
            mutableStateOf(
                TextFieldValue(
                    note ?: "",
                    TextRange(note?.length ?: 0)
                )
            )
        }

    val modalHeader = if (note.isNullOrBlank()) {
        "Add note"
    } else {
        "Edit note"
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
                    modalHeader,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = modifier.align(Alignment.CenterHorizontally),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
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
                    value = noteFieldValue.value,
                    onValueChange = { noteFieldValue.value = it },
                    label = { Text(stringResource(R.string.note)) },
                    singleLine = false,
                )
                Button(
                    onClick = {
                        saveNote(noteFieldValue.value.text)
                        onDismissRequest()
                    },
                    enabled = true,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}