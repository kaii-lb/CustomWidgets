@file:Suppress("SpellCheckingInspection")

package com.kaii.customwidgets

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth

class AnalogClockWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val clockIntent = Intent(
                    Intent.ACTION_MAIN,
                )
                clockIntent.setComponent(ComponentName.unflattenFromString("com.google.android.deskclock/com.android.deskclock.DeskClock"))

                Column (
                    modifier = GlanceModifier
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                ) {
                    Column (
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .cornerRadius(100.dp)
                            .clickable(actionStartActivity(clockIntent)),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                    ) {
                        val packageName = LocalContext.current.packageName
                        val remoteViews = RemoteViews(packageName, R.layout.analog_clock_layout)

                        AndroidRemoteViews(remoteViews)
                    }
                }
            }
        }
    }
}

class AnalogClockWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AnalogClockWidget()
}