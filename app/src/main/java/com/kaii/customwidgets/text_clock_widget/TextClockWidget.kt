package com.kaii.customwidgets.text_clock_widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.LocalTime
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.time.Instant

class TextClockWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    companion object {
        private val ONE_TWO = DpSize(40.dp, 110.dp)
        private val TWO_ONE = DpSize(110.dp, 40.dp)
        private val TWO_TWO = DpSize(110.dp, 110.dp)
        private val THREE_TWO = DpSize(180.dp, 110.dp)
      	private val FOUR_THREE = DpSize(250.dp, 180.dp)                

      	var alreadyUpdated = false
		var lastMinute = 0
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(
            ONE_TWO, TWO_ONE, TWO_TWO, THREE_TWO, FOUR_THREE
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        if (!alreadyUpdated) {
			val updateIntent = Intent(context, TextClockWidgetReceiver::class.java).apply {
	            action = TextClockWidgetReceiver.UPDATE_TEXT_CLOCK_ACTION
	        }
        	context.sendBroadcast(updateIntent)
        	alreadyUpdated = true
        }
    	
        provideContent {
            GlanceTheme {
            	val size = LocalSize.current

				if (size.width == TWO_ONE.width && size.height == TWO_ONE.height) {
					HorizontalContent(52f)
				}
            	else if (size.width <= ONE_TWO.width && size.height <= ONE_TWO.height) {
            		VerticalContent(36f)
            	}
            	else if (size.width <= TWO_TWO.width && size.height <= TWO_TWO.height) {
            		VerticalContent(64f)
            	}
				else if (size.width <= THREE_TWO.width && size.height <= THREE_TWO.height) {
					HorizontalContent(72f)
				}
				else if (size.width == FOUR_THREE.width && size.height >= FOUR_THREE.height) {
					HorizontalContent(84f)
				}
				else if (size.width >= THREE_TWO.width && size.height <= THREE_TWO.height) {
					VerticalContent(96f)
				}
            	else {
            		VerticalContent(72f)	
            	}
            }
        }
    }

	@Composable
	private fun HorizontalContent(textSize: Float) {
		Row (
            modifier = GlanceModifier
                .appWidgetBackground()
                .background(ColorProvider(Color.Transparent))
                .cornerRadius(48.dp)
                .fillMaxSize()
                .clickable(actionRunCallback(ForceUpdateTextClockWidget::class.java)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val hourFormat = DateTimeFormatter.ofPattern("hh")
            val minuteFormat = DateTimeFormatter.ofPattern("mm")

			val now = LocalTime.now()
            val hours = now.format(hourFormat)
            var minutes = now.format(minuteFormat)

			println("MINUTES IS $minutes, LAST MINUTE IS $lastMinute")
			if (minutes == lastMinute.toString()) {
				println("MATCHED MINUTES WITH LAST MINUTE $lastMinute")
                minutes = if (lastMinute == -1) {
                    "00"
                } else {
                    (minutes.toInt() + 1).toString().padStart(1, '0')
                }
			}

			val hourFirst = hours[0].toString()
			val hourSecond = hours[1].toString()

			val minuteFirst = minutes[0].toString()
			val minuteSecond = minutes[1].toString()

            Text(
                text = hourFirst,
                maxLines = 1,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = GlanceTheme.colors.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(textSize, TextUnitType.Sp),
                )
            )

			Text(
                text = hourSecond,
                maxLines = 1,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = GlanceTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(textSize, TextUnitType.Sp),
                )
            )

			Text(
				text = ":",
				maxLines = 1,
	            style = TextStyle(
	                textAlign = TextAlign.Center,
	                color = GlanceTheme.colors.onBackground,
	                fontWeight = FontWeight.Bold,
	                fontSize = TextUnit(textSize, TextUnitType.Sp),
	            )
			)

			Text(
                text = minuteFirst,
                maxLines = 1,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = GlanceTheme.colors.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(textSize, TextUnitType.Sp),
                )
            )

            Text(
                text = minuteSecond,
                maxLines = 1,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = GlanceTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(textSize, TextUnitType.Sp),
                )
            )
        }
	}

    @Composable
    private fun VerticalContent(textSize: Float) {
        Column (
            modifier = GlanceModifier
                .appWidgetBackground()
                .background(ColorProvider(Color.Transparent))
                .cornerRadius(48.dp)
                .fillMaxSize()
				.clickable(actionRunCallback(ForceUpdateTextClockWidget::class.java)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val hourFormat = DateTimeFormatter.ofPattern("hh")
            val minuteFormat = DateTimeFormatter.ofPattern("mm")

   			val now = LocalTime.now()
            val hours = now.format(hourFormat)
            var minutes = now.format(minuteFormat)

//			println("MINUTES IS $minutes")
            if (minutes == lastMinute.toString()) {
                println("MATCHED MINUTES WITH LAST MINUTE $lastMinute")
                minutes = (minutes.toInt() + 1).toString()
            }

			val hourFirst = hours[0].toString()
			val hourSecond = hours[1].toString()

			val minuteFirst = minutes[0].toString()
			val minuteSecond = minutes[1].toString()

			Row {	
	            Text(
	                text = hourFirst,
	                maxLines = 1,
	                style = TextStyle(
	                    textAlign = TextAlign.Center,
	                    color = GlanceTheme.colors.onBackground,
	                    fontWeight = FontWeight.Bold,
	                    fontSize = TextUnit(textSize, TextUnitType.Sp),
	                )
	            )

	            Text(
	                text = hourSecond,
	                maxLines = 1,
	                style = TextStyle(
	                    textAlign = TextAlign.Center,
	                    color = GlanceTheme.colors.primary,
	                    fontWeight = FontWeight.Bold,
	                    fontSize = TextUnit(textSize, TextUnitType.Sp),
	                )
	            )
			}

            Row {
            	Text(
   	                text = minuteFirst,
   	                maxLines = 1,
   	                style = TextStyle(
   	                    textAlign = TextAlign.Center,
   	                    color = GlanceTheme.colors.onBackground,
   	                    fontWeight = FontWeight.Bold,
   	                    fontSize = TextUnit(textSize, TextUnitType.Sp),
   	                )
   	            )

				Text(
	                text = minuteSecond,
	                maxLines = 1,
	                style = TextStyle(
	                    textAlign = TextAlign.Center,
	                    color = GlanceTheme.colors.primary,
	                    fontWeight = FontWeight.Bold,
	                    fontSize = TextUnit(textSize, TextUnitType.Sp),
	                )
	            )
            }           
        }
    }
}

class TextClockWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TextClockWidget()

    companion object {
        const val UPDATE_TEXT_CLOCK_ACTION = "com.kaii.customwidgets.text_clock_widget.UPDATE_TEXT_CLOCK_ACTION"
       	const val FORCE_UPDATE_TEXT_CLOCK_ACTION = "com.kaii.customwidgets.text_clock_widget.FORCE_UPDATE_TEXT_CLOCK_ACTION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == UPDATE_TEXT_CLOCK_ACTION) {
            val now = LocalTime.now()

            // var neededTimeToUpdate = 60 - now.second
			var neededTimeToUpdate = Duration.between(now, now.plusMinutes(1).withSecond(0).withNano(0)).toMillis()

            if (neededTimeToUpdate <= 0) {
                neededTimeToUpdate = 1000
            }

            println("NEEDED TIME TO UPDATE $neededTimeToUpdate milliseconds")

			runBlocking {
				TextClockWidget().updateAll(context)
				TextClockWidget().updateAll(context)			
			}

            val minuteFormat = DateTimeFormatter.ofPattern("mm")
            TextClockWidget.lastMinute = now.format(minuteFormat).toInt() - 1

            val handler = Handler(Looper.getMainLooper())

            val runnable = Runnable {
                val updateIntent = Intent(context, TextClockWidgetReceiver::class.java).apply {
                    action = UPDATE_TEXT_CLOCK_ACTION
                }
                context.sendBroadcast(updateIntent)
            }
            handler.removeCallbacks(runnable)
            handler.postDelayed(runnable, neededTimeToUpdate)
        }
        else if(intent.action == FORCE_UPDATE_TEXT_CLOCK_ACTION) {
        	MainScope().launch {
                val manager = GlanceAppWidgetManager(context)
                val ids = manager.getGlanceIds(TextClockWidget::class.java)

                ids.forEach { glanceID ->
                    glanceID.let {
                        glanceAppWidget.update(context, it)
                    }
                }
            }

			// TextClockWidget.alreadyUpdated = false
            println("UPDATED THRU CLICK")
      	}	
    }
}
