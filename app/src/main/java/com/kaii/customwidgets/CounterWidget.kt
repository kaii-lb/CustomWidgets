package com.kaii.customwidgets

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import java.io.File

object CounterWidget : GlanceAppWidget() {

    val countKey = intPreferencesKey("count")

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                CounterContent()
            }
        }
    }

    @Composable
    fun CounterContent() {
        val count = currentState(key = countKey) ?: 0

        Column (
            modifier = GlanceModifier
                .padding(10.dp)
                .fillMaxSize()
                .cornerRadius(16.dp)
                .background(GlanceTheme.colors.widgetBackground),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        ) {
            Text(
                text = count.toString(),
//                GlanceModifier.fillMaxSize(),
                style = TextStyle(
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily("ndot55"),
                    color = ColorProvider(Color.White),
                    fontSize = 26.sp,
                )
            )

            Button(
                text = "Press Me!",
                onClick = actionRunCallback(IncrementActionCallback::class.java),
//                GlanceModifier.fillMaxSize(),
            )
        }
    }
}

class SimpleCounterWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CounterWidget
}

class IncrementActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) {prefs ->
            val currentCount = prefs[CounterWidget.countKey]

            if (currentCount != null) {
                prefs[CounterWidget.countKey] = currentCount + 1
//                prefs[CounterWidget.torchMode] = prefs[CounterWidget.torchMode] != true
            }
            else {
                prefs[CounterWidget.countKey] = 1
            }
//            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
//            val cameraID = cameraManager.cameraIdList[1]
//
//            prefs[CounterWidget.torchMode]?.let { cameraManager.setTorchMode(cameraID, it) }
        }

        CounterWidget.update(context, glanceId)
    }
}
