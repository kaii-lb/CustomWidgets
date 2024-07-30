@file:Suppress("SpellCheckingInspection")

package com.kaii.customwidgets.analog_clock

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.widget.RemoteViews
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
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
import androidx.glance.layout.fillMaxWidth
import com.kaii.customwidgets.R

class AnalogClockWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                val clockIntent = Intent(
                    Intent.ACTION_MAIN,
                )

                // TODO: change to launch default clock app
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
                        val colors = GlanceTheme.colors
                        val localContext = LocalContext.current

                        val dialColor by remember {
                            mutableIntStateOf(colors.widgetBackground.getColor(localContext).toArgb())
                        }
                        remoteViews.setColorStateList(
                            R.id.analog_clock_id,
                            "setDialTintList",
                            ColorStateList.valueOf(
                                dialColor
                            )
                        )

                        val secondColor by remember {
                            mutableIntStateOf(colors.tertiary.getColor(localContext).toArgb())
                        }
                        remoteViews.setColorStateList(
                            R.id.analog_clock_id,
                            "setSecondHandTintList",
                            ColorStateList.valueOf(
                                secondColor
                            )
                        )

                        val minuteColor by remember {
                            mutableIntStateOf(colors.primary.getColor(localContext).toArgb())
                        }
                        remoteViews.setColorStateList(
                            R.id.analog_clock_id,
                            "setMinuteHandTintList",
                            ColorStateList.valueOf(
                                minuteColor
                            )
                        )

                        val hourColor by remember {
                            mutableIntStateOf(colors.inversePrimary.getColor(localContext).toArgb())
                        }
                        remoteViews.setColorStateList(
                            R.id.analog_clock_id,
                            "setHourHandTintList",
                            ColorStateList.valueOf(
                                hourColor
                            )
                        )

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
