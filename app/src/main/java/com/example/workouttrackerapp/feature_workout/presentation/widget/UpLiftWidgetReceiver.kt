package com.example.workouttrackerapp.feature_workout.presentation.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.workouttrackerapp.di.AppModule
import com.example.workouttrackerapp.feature_workout.domain.model.helper.WeekStartDay
import com.example.workouttrackerapp.feature_workout.domain.use_case.WorkoutUseCases
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@AndroidEntryPoint
class UpLiftWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = UpLiftWidget()

    private val coroutineScope = MainScope()

    @Inject
    lateinit var workoutUseCases: WorkoutUseCases

    @Inject
    lateinit var dataStoreManager: AppModule.DataStoreManager

    private fun observeData(context: Context) {
        coroutineScope.launch {
            // Collect the latest value from widgetData
            val widgetData = workoutUseCases.getWidgetData().first()

            // Collect the latest value from weekStartDay
            val weekStartDay = dataStoreManager.weekStartDay.first()

            // Collect dynamic color value
            val isDynamicTheme = dataStoreManager.dynamicColors.first()

            // Collect dynamic primary color
            val primaryDynamicColor = dataStoreManager.primaryDynamicColor.first()

            // Calculate the padding based on the collected values
            val currentDay = LocalDate.now().dayOfWeek
            val nullPadding = calculateNullPadding(
                currentDay.value,
                WeekStartDay.fromValue(weekStartDay)?.value ?: WeekStartDay.SUNDAY.value
            )

            // Create the new widget data, adding the necessary null padding
            val newWidgetData: List<Boolean?> =
                (widgetData + List(nullPadding) { null }).takeLast(70)

            // Serialize the updated data
            val serializedHistory = Gson().toJson(newWidgetData)
            val serializedDynamicColor = Gson().toJson(isDynamicTheme)
            val serializedPrimaryDynamicColor = Gson().toJson(primaryDynamicColor)

            // Get the widget's glance IDs
            val glanceIds = GlanceAppWidgetManager(context).getGlanceIds(UpLiftWidget::class.java)

            // Update the widget state and refresh the widget
            glanceIds.forEach { id ->
                updateAppWidgetState(
                    context,
                    PreferencesGlanceStateDefinition,
                    id
                ) { prefs ->
                    prefs.toMutablePreferences().apply {
                        this[widgetDataKey] = serializedHistory
                        this[dynamicColorKey] = serializedDynamicColor
                        this[primaryDynamicColorKey] = serializedPrimaryDynamicColor
                    }
                }
            }

            // Trigger the widget update
            glanceAppWidget.updateAll(context)
        }
    }

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        Log.d("UpLiftWidgetReceiver", "onUpdate Triggered")
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        observeData(context)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        observeData(context)

    }

    private fun calculateNullPadding(
        currentDay: Int,
        weekStartDay: Int
    ): Int {
        val offset = (6 - ((currentDay - weekStartDay + 7) % 7))
        return offset
    }


    companion object {
        val widgetDataKey = stringPreferencesKey("widgetData")
        val dynamicColorKey = stringPreferencesKey("dynamicColor")
        val primaryDynamicColorKey = stringPreferencesKey("primaryDynamicColor")

    }

}
