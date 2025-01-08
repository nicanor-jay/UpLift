package com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.domain.model.Exercise
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.WorkoutTextFieldState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectExerciseModal(
    modifier: Modifier = Modifier,
    exerciseList: List<Exercise>,
    onClick: (Int) -> Unit,
    queryState: WorkoutTextFieldState,
    onQueryChange: (String) -> Unit,
    onDismissRequest: () -> Unit,
    focusSearchOnToggle: Boolean = true,
) {

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (focusSearchOnToggle) {
            focusRequester.requestFocus()
        }
    }

    ModalBottomSheet(sheetState = bottomSheetState, onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                "Exercises",
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

            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                windowInsets = WindowInsets(top = 0.dp),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = queryState.text,
                        onQueryChange = { onQueryChange(it) },
                        onSearch = { },
                        expanded = false,
                        onExpandedChange = { },
                        placeholder = { Text(queryState.hint) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
//                            trailingIcon = {
//                                Icon(
//                                    Icons.Default.MoreVert,
//                                    contentDescription = null
//                                )
//                            },
                    )
                },
                expanded = false,
                onExpandedChange = {},
            ) {}

            Spacer(modifier = modifier.height(dimensionResource(R.dimen.padding_extra_small)))

            LazyColumn() {
                items(
                    exerciseList,
                    key = { item -> item.id!! }) { exercise ->
                    ExerciseWithDescriptionItem(
                        modifier = Modifier,
                        exercise,
                        onClick = { onClick(it) })
                }
            }
        }
    }
}