package com.example.workouttrackerapp.feature_workout.presentation.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.material3.ColorProviders
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.workouttrackerapp.MainActivity
import com.example.workouttrackerapp.ui.theme.Purple40
import com.example.workouttrackerapp.ui.theme.Purple80
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UpLiftWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override val sizeMode = SizeMode.Exact

    object MyWidgetTheme {
        val colors = ColorProviders(
            // Makeshift color schemes for widget
            light = lightColorScheme(
                primary = Purple40,
                secondary = Color.LightGray,

                ),
            dark = darkColorScheme(
                primary = Purple80,
                secondary = Color.DarkGray,
            )
        )
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {

        // In this method, load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.

        provideContent {
            GlanceTheme(
                colors = MyWidgetTheme.colors
            ) {
                MyContent()
            }
        }
    }

    @Composable
    private fun MyContent() {
        val context = LocalContext.current
        val numWeeks = 9
        val numDays = numWeeks * 7
        val deserializedList = currentState(key = UpLiftWidgetReceiver.widgetDataKey)
        val deserializedDynamicColor = currentState(key = UpLiftWidgetReceiver.dynamicColorKey)
        val deserializedPrimaryDynamicColor =
            currentState(key = UpLiftWidgetReceiver.primaryDynamicColorKey)

        var widgetData = Gson().fromJson<List<Boolean?>>(
            deserializedList, object : TypeToken<List<Boolean?>>() {}.type
        )

        val isDynamicTheming = Gson().fromJson<Boolean>(
            deserializedDynamicColor, object : TypeToken<Boolean>() {}.type
        )

        val dynamicPrimaryColor = Gson().fromJson<Int>(
            deserializedPrimaryDynamicColor, object : TypeToken<Int>() {}.type
        )

        if (widgetData.isNullOrEmpty()) {
            widgetData = List(numDays) {
                false
            }
        }
        if (isDynamicTheming == null || dynamicPrimaryColor == null ) {
            return
        }

        Log.d("UpLiftWidget", "dynamicColor : ${isDynamicTheming.toString()}")
        Log.d("UpLiftWidget", "primaryDynamicColor : ${dynamicPrimaryColor.toString()}")
        Log.d("UpLiftWidget", "GlanceTheme primary : ${GlanceTheme.colors.primary.toString()}")

        val colorTest = Color(dynamicPrimaryColor)

        val primaryColor =
            if (isDynamicTheming) ColorProvider(colorTest) else  GlanceTheme.colors.primary

        Log.d("UpLiftWidget", widgetData.toString())

        Row(
            modifier = GlanceModifier.fillMaxSize().padding(10.dp)
                .background(
//                    ColorProvider(colorTest)
                    GlanceTheme.colors.surface
                )
                .clickable(
                    onClick = actionStartActivity(
                        Intent(
                            context,
                            MainActivity::class.java
                        )
                    )
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val cols = widgetData.takeLast(numDays).chunked(7)
            for (col in cols) {
                Column(
                    modifier = GlanceModifier.defaultWeight()
                ) {
                    for (item in col) {
                        if (item != null) {
                            val color =
                                if (item)
                                    primaryColor else
                                    GlanceTheme.colors.secondary
                            Box(
                                modifier = GlanceModifier
                                    //.height(15.dp)
                                    //.width(15.dp)
                                    .defaultWeight()
                                    .padding(2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "",
                                    style = TextStyle(
                                        color = ColorProvider(Color.Transparent),
                                        fontSize = 12.sp
                                    ),
                                    modifier = GlanceModifier.padding(4.dp)
                                        .background(color).fillMaxSize()
                                        .cornerRadius(2.dp),

                                    )
                            }
                        } else {
                            Box(
                                modifier = GlanceModifier
                                    //.height(15.dp)
                                    //.width(15.dp)
                                    .defaultWeight()
                                    .padding(2.dp)
                                    .cornerRadius(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "",
                                    style = TextStyle(
                                        color = ColorProvider(Color.Transparent),
                                        fontSize = 12.sp
                                    ),
                                    modifier = GlanceModifier.padding(4.dp)
                                        .background(GlanceTheme.colors.surface).fillMaxSize()
                                        .cornerRadius(2.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}