package com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.domain.model.helper.RoutineWithExerciseCount
import com.example.workouttrackerapp.feature_workout.presentation.routine.components.RoutineItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportRoutineModal(
    modifier: Modifier = Modifier,
    routineList: List<RoutineWithExerciseCount>,
    onClick: (Int) -> Unit,
    onDismissRequest: () -> Unit
) {

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(sheetState = bottomSheetState, onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .fillMaxSize()
        ) {
            Text(
                "Select Routine",
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

            Spacer(modifier = modifier.height(dimensionResource(R.dimen.padding_extra_small)))

            LazyColumn() {
                items(
                    routineList,
                    key = { item -> item.routineId }) { routine ->
                    RoutineItem(
                        modifier,
                        routine,
                        { onClick(routine.routineId) },
                        false,
                        {}
                    )
                }
            }
        }
    }


}