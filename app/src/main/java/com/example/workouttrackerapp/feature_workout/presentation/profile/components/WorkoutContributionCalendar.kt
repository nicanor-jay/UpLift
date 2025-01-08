package com.example.workouttrackerapp.feature_workout.presentation.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import androidx.navigation.NavController
import com.example.workouttrackerapp.AddEditWorkout
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.domain.model.Workout
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WeekStartDay
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WorkoutContribution
import com.example.workouttrackerapp.ui.theme.EmptyGray

@Composable
fun WorkoutContributionCalendar(
    modifier: Modifier? = Modifier,
    navController: NavController,
    weekStartDay: WeekStartDay = WeekStartDay.SUNDAY,
    contributionList: List<WorkoutContribution>
) {
    val numRows = 8
    val spacedBy = 3
    val roundedCornerSize = 4
    val itemSize = 40
    val rowHeight = ((numRows * itemSize) + (spacedBy * (numRows - 1)))
    val lazyGridState = rememberLazyGridState()

    val fullDayList = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    val startIndex = when (weekStartDay) {
        WeekStartDay.SUNDAY -> 0
        WeekStartDay.MONDAY -> 1
        WeekStartDay.SATURDAY -> 6
    }

    val dayList = listOf("") + fullDayList.slice(startIndex until fullDayList.size) +
            fullDayList.slice(0 until startIndex)

    var effectExecuted by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!effectExecuted) {
            lazyGridState.scrollToItem(contributionList.lastIndex)
            effectExecuted = true
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                dimensionResource(R.dimen.padding_medium)
            ),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "Calendar",
            modifier = Modifier
                .fillMaxWidth(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier
                .height(rowHeight.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LazyHorizontalGrid(
                rows = GridCells.Fixed(numRows),
                horizontalArrangement = Arrangement.spacedBy(spacedBy.dp),
                verticalArrangement = Arrangement.spacedBy(spacedBy.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = lazyGridState,
            ) {
                itemsIndexed(contributionList) { index, item ->
                    var isDropdownMenuVisible by remember { mutableStateOf(false) }
                    var pressOffset by remember {
                        mutableStateOf(DpOffset.Zero)
                    }
                    Box(
                        modifier = Modifier.pointerInput(true) {
                            detectTapGestures(
                                onTap = {
                                    if (item.formattedDate != null) {
                                        isDropdownMenuVisible = true
                                        pressOffset = DpOffset(it.x.toDp(), it.y.toDp())
                                    }
                                }
                            )
                        }
                    ) {
                        Text(
                            item.monthLabel ?: (item.formattedDate?.substring(0, 2) ?: ""),
                            color = if (item.formattedDate == null || (index % 7 + 1) == 0) {
                                MaterialTheme.colorScheme.onSurface
                            } else if (item.hasContribution) MaterialTheme.colorScheme.onPrimary else Color.White,
                            modifier =
                            Modifier
                                .background(
                                    color =
                                    if (item.formattedDate == null || (index % 7 + 1) == 0) Color.Transparent else if (item.hasContribution) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        EmptyGray
                                    },
                                    shape = RoundedCornerShape(roundedCornerSize.dp)
                                )
                                .size(itemSize.dp)
                                .wrapContentSize(),
                            textAlign = TextAlign.Center,
                        )
                        DropdownMenu(
                            isDropdownMenuVisible,
                            onDismissRequest = { isDropdownMenuVisible = false },
                            offset = pressOffset.copy(
                                x = pressOffset.x - itemSize.dp,
                                y = pressOffset.y - (4 * itemSize).dp
                            ),
                            modifier = Modifier
//                                .width((itemSize * 4).dp)
                                .padding(dimensionResource(R.dimen.padding_small))
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    item.formattedDate.toString(),
                                    style = MaterialTheme.typography.titleMedium
                                )

                                item.workoutList?.forEachIndexed { index, item ->
                                    val textColor =
                                        if (ColorUtils.calculateLuminance(Workout.workoutColors[item.workoutColor!!].toArgb()) < 0.5) {
                                            Color.White
                                        } else {
                                            Color.Black
                                        }

                                    Card(
                                        modifier = Modifier
                                            .padding(
                                                dimensionResource(R.dimen.padding_extra_small)
                                            )
                                            .clickable {
                                                isDropdownMenuVisible = false
                                                navController.navigate(
                                                    AddEditWorkout(
                                                        item.workoutId!!,
                                                        item.workoutColor!!,
                                                    )
                                                )
                                            },
                                        colors = CardDefaults.cardColors(containerColor = Workout.workoutColors[item.workoutColor!!])
                                    ) {
                                        Text(
                                            (index + 1).toString() + ": " + item.numExercises.toString() + " exercises",
                                            modifier = Modifier
                                                .padding(
                                                    dimensionResource(R.dimen.padding_small)
                                                ),
                                            color = textColor
                                        )
                                    }
                                }
                                if (item.workoutList.isNullOrEmpty()) {
                                    Text(stringResource(R.string.no_data))
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(spacedBy.dp))
            Column(
                modifier = Modifier.height(rowHeight.dp),
                verticalArrangement = Arrangement.spacedBy(spacedBy.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                dayList.forEachIndexed { _, day ->
                    Text(
                        text = day,
                        Modifier
                            .background(
                                Color.Transparent, RoundedCornerShape(roundedCornerSize.dp)
                            )
                            .height(itemSize.dp)
                            .wrapContentSize(),
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}