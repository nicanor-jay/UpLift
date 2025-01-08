package com.example.workouttrackerapp.feature_workout.presentation.progression

import android.text.Layout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.workouttrackerapp.R
import com.example.workouttrackerapp.feature_workout.presentation.add_edit_workout.components.SelectExerciseModal
import com.example.workouttrackerapp.feature_workout.presentation.progression.components.CheckboxWithLabel
import com.example.workouttrackerapp.feature_workout.presentation.progression.components.RecordContributionCalendar
import com.example.workouttrackerapp.feature_workout.presentation.progression.components.rememberMarker
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberTop
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.data.rememberExtraLambda
import com.patrykandpatrick.vico.compose.common.dimensions
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.rememberHorizontalLegend
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.LegendItem
import com.patrykandpatrick.vico.core.common.component.TextComponent
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


private val chartColors = listOf(Color(0xffb983ff), Color(0xff91b1fd), Color(0xff8fdaff))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressionScreen(
    navController: NavController,
    viewModel: ProgressionViewModel = hiltViewModel()
) {
    val selectExerciseModalState = viewModel.isSelectExerciseModalVisible.value
    val queriedExercisesList = viewModel.queriedExerciseList.value
    val queryState = viewModel.searchQuery.value
    val exerciseRecordHistory = viewModel.exerciseRecords.value
    val contributionRecord = viewModel.contributionRecords.value
    val weekStartDay = viewModel.weekStartDay.value
    val weightUnitLabel = viewModel.weightUnit.value

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()


    val selectedExercise = viewModel.selectedExercise.value

    val scrollState = rememberVicoScrollState(
        initialScroll = Scroll.Absolute.End
    )
    val zoomState = rememberVicoZoomState()
    val textColor: Int = MaterialTheme.colorScheme.onSurface.toArgb()

    val checkBoxStates = viewModel.checkboxStates.value

    val topAppBarTitle = if (selectedExercise == null) {
        "Progression"
    } else {
        "Progression - ${selectedExercise.name}"
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.onEvent(ProgressionEvent.ToggleSelectExerciseModal)
                }
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    "Select Exercise",
                )
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.padding_extra_small)))
                if (selectedExercise == null) {
                    Text(stringResource(R.string.select_exercise))
                } else {
                    Text(selectedExercise.name)
                }
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(topAppBarTitle, color = MaterialTheme.colorScheme.primary) },
                scrollBehavior = scrollBehavior
            )
        },
    ) { padding ->

        if (selectedExercise == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stringResource(R.string.no_exercise_selected),
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        } else if (exerciseRecordHistory.isNullOrEmpty() || exerciseRecordHistory.size < 2) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Not enough data...",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }
            return@Scaffold
        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_small))
                ) {
                    val activeLines = checkBoxStates.entries
                        .filter { it.value } // Only include active options
                        .mapIndexed { index, (option, _) ->
                            LineCartesianLayer.rememberLine(
                                fill = remember {
                                    LineCartesianLayer.LineFill.single(
                                        fill(chartColors[index % chartColors.size])
                                    )
                                }
                            )
                        }

                    CartesianChartHost(
                        modifier = Modifier
                            .padding(dimensionResource(R.dimen.padding_small)),
                        scrollState = scrollState,
                        zoomState = zoomState,
                        chart = rememberCartesianChart(
                            rememberLineCartesianLayer(
                                LineCartesianLayer.LineProvider.series(activeLines)
                            ),
                            startAxis = VerticalAxis.rememberStart(
                            ),
                            bottomAxis = HorizontalAxis.rememberBottom(
                                titleComponent =
                                rememberTextComponent(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    margins = dimensions(end = 4.dp),
                                    padding = dimensions(8.dp, 2.dp),
                                    background = rememberShapeComponent(
                                        Color.Transparent,
                                        CorneredShape.Pill
                                    ),
                                ),
                                title = "Date",
                                valueFormatter = viewModel.valueFormatter.value
                            ),
                            topAxis = HorizontalAxis.rememberTop(
                                titleComponent = rememberTextComponent(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    margins = dimensions(end = 4.dp),
                                    padding = dimensions(8.dp, 2.dp),
                                    background = rememberShapeComponent(
                                        Color.Transparent,
                                        CorneredShape.Pill
                                    ),
                                    textSize = MaterialTheme.typography.titleMedium.fontSize,
                                    textAlignment = Layout.Alignment.ALIGN_NORMAL
                                ),
                                title = "Reps/Weight/Intensity Over Time",
                                label = null,
                                line = null,
                                tick = null,
                                guideline = null
                            ),
                            legend = rememberHorizontalLegend(
                                items = rememberExtraLambda(checkBoxStates.values) {
                                    var colorIndex =
                                        0 // Track colors dynamically based on active options
                                    if (checkBoxStates[CheckboxOption.OPTION_ONE] == true) {
                                        add(
                                            LegendItem(
                                                icon = shapeComponent(
                                                    chartColors[colorIndex++ % chartColors.size],
                                                    CorneredShape.Pill
                                                ),
                                                labelComponent = TextComponent(textColor),
                                                label = "Weight",
                                            )
                                        )
                                    }
                                    if (checkBoxStates[CheckboxOption.OPTION_TWO] == true) {
                                        add(
                                            LegendItem(
                                                icon = shapeComponent(
                                                    chartColors[colorIndex++ % chartColors.size],
                                                    CorneredShape.Pill
                                                ),
                                                labelComponent = TextComponent(textColor),
                                                label = "Reps",
                                            )
                                        )
                                    }
                                    if (checkBoxStates[CheckboxOption.OPTION_THREE] == true) {
                                        add(
                                            LegendItem(
                                                icon = shapeComponent(
                                                    chartColors[colorIndex++ % chartColors.size],
                                                    CorneredShape.Pill
                                                ),
                                                labelComponent = TextComponent(textColor),
                                                label = "Intensity",
                                            )
                                        )
                                    }
                                }
                            ),
                            marker = rememberMarker()
                        ),
                        modelProducer = viewModel.modelProducer
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = dimensionResource(R.dimen.padding_small)),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        CheckboxOption.entries.forEach { option ->
                            val isChecked = checkBoxStates[option] ?: false
                            CheckboxWithLabel(
                                label = option.label,
                                isChecked = isChecked,
                                onCheckedChange = { it ->
                                    viewModel.onEvent(
                                        ProgressionEvent.ToggleCheckbox(
                                            option,
                                            it
                                        )
                                    )
                                }
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_small))
                ) {

                    if (selectedExercise != null) {
                        if (contributionRecord != null) {
                            RecordContributionCalendar(
                                modifier = Modifier,
                                exerciseString = selectedExercise.name,
                                contributionList = contributionRecord,
                                weekStartDay = weekStartDay,
                                weightUnitLabel = weightUnitLabel,
                                navController = navController
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2 * FloatingActionButtonDefaults.LargeIconSize))

            }
        }
    }


    if (selectExerciseModalState) {
        SelectExerciseModal(
//                modifier = Modifier,
            exerciseList = queriedExercisesList,
            onClick = { viewModel.onEvent(ProgressionEvent.SelectExercise(it)) },
            onDismissRequest = { viewModel.onEvent(ProgressionEvent.ToggleSelectExerciseModal) },
            queryState = queryState,
            onQueryChange = { viewModel.onEvent(ProgressionEvent.EnteredSearch(it)) }
        )
    }

}

fun convertTimestampToLocalDate(timestamp: Long): LocalDate {
    // Convert the timestamp (in milliseconds) to an Instant
    val instant = Instant.ofEpochMilli(timestamp)

    // Convert the Instant to a ZonedDateTime in UTC
    val zonedDateTime = instant.atZone(ZoneId.systemDefault())

    // Extract the LocalDate part
    return zonedDateTime.toLocalDate()
}
