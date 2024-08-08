package com.kaii.customwidgets.weather_widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.Spacer
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.kaii.customwidgets.text_clock_widget.ForceUpdateTextClockWidget
import fuel.Fuel
import fuel.get
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.time.LocalTime
import java.time.Duration
import java.time.format.DateTimeFormatter

class WeatherWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    companion object {
        private val TWO_ONE = DpSize(110.dp, 40.dp)
        private val THREE_ONE = DpSize(180.dp, 40.dp)

        var alreadyUpdated = false
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(
            TWO_ONE, THREE_ONE
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        if (!alreadyUpdated) {
            val updateIntent = Intent(context, WeatherWidgetReceiver::class.java).apply {
                action = WeatherWidgetReceiver.UPDATE_WEATHER_WIDGET_ACTION
            }
            context.sendBroadcast(updateIntent)
            alreadyUpdated = true
        }

        provideContent {
            GlanceTheme {
                SmallContent()
            }
        }
    }

    @Composable
    private fun SmallContent() {
        Column (
            modifier = GlanceModifier
                .background(ColorProvider(Color.Transparent))
                .cornerRadius(1000.dp)
                .padding(0.dp, 8.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val size = LocalSize.current

            val weatherIntent = Intent(
                Intent.ACTION_MAIN,
            )
            weatherIntent.setComponent(
                ComponentName.unflattenFromString(
                    "com.google.android.googlequicksearchbox/com.google.android.apps.search.weather.WeatherExportedActivity"
                )
            )

            Row (
                modifier = GlanceModifier
                    .background(GlanceTheme.colors.widgetBackground)
                    .appWidgetBackground()
                    .cornerRadius(1000.dp)
                    .fillMaxSize()
                    .clickable(actionStartActivity(weatherIntent)),
	            verticalAlignment = Alignment.CenterVertically,
	            horizontalAlignment = Alignment.CenterHorizontally                    
            ) {
                val responseJson = runBlocking {
                    Fuel.get("https://wttr.in/Salima+Lebanon?format=j1").body.string()
                }

                // val weatherState = response.split(" ")[0]
                // val weatherTemperature = response.split(" ")[1].replace("+", "")

				

				val longBoi = size.width >= THREE_ONE.width

				// please find a way to not var these
				var neededWidth = 0.dp
				var neededSpacing = 0.dp
				val neededTextSize: Float
				if (longBoi) {
					neededWidth = size.width / 2 - 8.dp
					neededSpacing = 12.dp
					neededTextSize = 26f
				}
				else {
					neededWidth = size.width - size.height
					neededSpacing = 4.dp
					neededTextSize = 24f
				}

                Image(
                    provider = ImageProvider(com.kaii.customwidgets.R.drawable.clear),
                    contentDescription = "Shows the current weather in an icon",
                    modifier = GlanceModifier
                        .size(size.height)
                )

                Spacer (modifier = GlanceModifier.width(neededSpacing))

                Text (
                    text = weatherState,
                    maxLines = 1,
                    style = TextStyle(
                   		textAlign = TextAlign.Center,
                   		color = GlanceTheme.colors.onBackground,
                   		fontWeight = FontWeight.Bold,
                   		fontSize = TextUnit(neededTextSize, TextUnitType.Sp),
               		),
                    modifier = GlanceModifier
                        .width(neededWidth)
                )

                if (longBoi) {
                	Text (
   	                    text = weatherTemperature,
   	                    maxLines = 1,
   	                    style = TextStyle(
   	                   		textAlign = TextAlign.Center,
   	                   		color = GlanceTheme.colors.onBackground,
   	                   		fontWeight = FontWeight.Bold,
   	                   		fontSize = TextUnit(neededTextSize, TextUnitType.Sp),
   	               		),
   	                    modifier = GlanceModifier
   	                        .width(neededWidth - 8.dp)
   	                )	
                }
            }
        }
    }
}

class WeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WeatherWidget()

    companion object {
        const val UPDATE_WEATHER_WIDGET_ACTION = "com.kaii.customwidgets.weather_widget.UPDATE_WEATHER_WIDGET_ACTION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == UPDATE_WEATHER_WIDGET_ACTION) {
            val now = LocalTime.now()

            // var neededTimeToUpdate = 60 - now.minute
            val nextHour = now.plusHours(1).withMinute(0).withSecond(0).withNano(0)
            var neededTimeToUpdate = Duration.between(now, nextHour).toMillis()

            if (neededTimeToUpdate <= 0) {
                neededTimeToUpdate = 1000
            }

            //println("WEATHER NEEDED TIME TO UPDATE $neededTimeToUpdate milliseconds")

            runBlocking {
                WeatherWidget().updateAll(context)
                WeatherWidget().updateAll(context)
            }

            val handler = Handler(Looper.getMainLooper())

            val runnable = Runnable {
                val updateIntent = Intent(context, WeatherWidgetReceiver::class.java).apply {
                    action = UPDATE_WEATHER_WIDGET_ACTION
                }
                context.sendBroadcast(updateIntent)
            }
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, neededTimeToUpdate)
        }
    }
}
