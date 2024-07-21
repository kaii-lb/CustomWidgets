package com.kaii.customwidgets.battery_monitor_widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import com.kaii.customwidgets.R
import com.kaii.customwidgets.status_widget.DateWidget

class BatteryMonitorWidget : GlanceAppWidget() {
	override val sizeMode = SizeMode.Exact
	
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                BatteryMonitor()
            }
        }
    }

    @Composable
    private fun BatteryMonitor() {
        Column (
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row (
                modifier = GlanceModifier
                    .fillMaxSize()
                    .cornerRadius(1000.dp)
                    .appWidgetBackground()
                    .background(GlanceTheme.colors.widgetBackground),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.Start
            ) {
           		val size = LocalSize.current
                Row(
                    modifier = GlanceModifier
                        .width(size.width / 2)
                        .fillMaxHeight()
                        .cornerRadius(1000.dp)
                        .background(GlanceTheme.colors.primary),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.End
                ) {
                    Image(
                        provider = ImageProvider(R.drawable.play),
                        contentDescription = "icon showing battery symbol",
                        contentScale = ContentScale.Fit,
                    )
                }
            }
        }
    }
}

class BatteryMonitorWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BatteryMonitorWidget()
}
