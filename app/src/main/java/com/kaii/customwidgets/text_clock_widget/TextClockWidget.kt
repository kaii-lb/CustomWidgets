package com.kaii.customwidgets.text_clock_widget

import android.content.Context
import android.content.Intent
import android.widget.TextClock
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.fillMaxSize
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.kaii.customwidgets.music_widget.MusicWidget
import com.kaii.customwidgets.music_widget.MusicWidgetReceiver.Companion.album
import com.kaii.customwidgets.music_widget.MusicWidgetReceiver.Companion.albumArt
import com.kaii.customwidgets.music_widget.MusicWidgetReceiver.Companion.artist
import com.kaii.customwidgets.music_widget.MusicWidgetReceiver.Companion.length
import com.kaii.customwidgets.music_widget.MusicWidgetReceiver.Companion.likedYoutubeVideo
import com.kaii.customwidgets.music_widget.MusicWidgetReceiver.Companion.position
import com.kaii.customwidgets.music_widget.MusicWidgetReceiver.Companion.queue
import com.kaii.customwidgets.music_widget.MusicWidgetReceiver.Companion.songTitle
import com.kaii.customwidgets.notification_listener_service.NotificationListenerCustomService
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.sql.Time
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date

class TextClockWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    companion object {
        private val ONE_TWO = DpSize(40.dp, 110.dp)
        private val TWO_ONE = DpSize(110.dp, 40.dp)
        private val TWO_TWO = DpSize(110.dp, 110.dp)
        private val THREE_TWO = DpSize(180.dp, 110.dp)
      	private val FOUR_THREE = DpSize(250.dp, 180.dp)                

      	var alreadyUpdated = false
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
					HorizontalContent(48f)
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
                .background(GlanceTheme.colors.widgetBackground)
                .cornerRadius(1000.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val hourFormat = DateTimeFormatter.ofPattern("HH")
            val minuteFormat = DateTimeFormatter.ofPattern("mm")

            val hours by remember { mutableStateOf( LocalTime.now().format(hourFormat) ) }
            val minutes by remember { mutableStateOf( LocalTime.now().format(minuteFormat) ) }

            Text(
                text = hours,
                maxLines = 1,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = GlanceTheme.colors.onBackground,
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
                text = minutes,
                maxLines = 1,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = GlanceTheme.colors.onBackground,
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
                .background(GlanceTheme.colors.widgetBackground)
                .cornerRadius(1000.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val hourFormat = DateTimeFormatter.ofPattern("KK")
            val minuteFormat = DateTimeFormatter.ofPattern("m")

            val hours by remember { mutableStateOf( LocalTime.now().format(hourFormat) ) }
            val minutes by remember { mutableStateOf( LocalTime.now().format(minuteFormat) ) }

            Text(
                text = hours,
                maxLines = 1,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = GlanceTheme.colors.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(textSize, TextUnitType.Sp),
                )
            )

            Text(
                text = minutes,
                maxLines = 1,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = GlanceTheme.colors.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(textSize, TextUnitType.Sp),
                )
            )
        }
    }
}

class TextClockWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TextClockWidget()

    companion object {
        const val UPDATE_TEXT_CLOCK_ACTION = "com.kaii.customwidgets.text_clock_widget.UPDATE_TEXT_CLOCK_ACTION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == UPDATE_TEXT_CLOCK_ACTION) {
            val now = LocalTime.now()
//            val currentMinute = now.minute
            val currentSecond = now.second

            val neededTimeToUpdate = 60 - currentSecond

			println("NEEDED TIME TO UPDATE $neededTimeToUpdate")

            MainScope().launch {
                val manager = GlanceAppWidgetManager(context)
                val ids = manager.getGlanceIds(TextClockWidget::class.java)

                ids.forEach { glanceID ->
                    glanceID.let {
                        glanceAppWidget.update(context, it)
                    }
                }
            }

            // Thread.sleep(neededTimeToUpdate.toLong() * 1000)

            val updateIntent = Intent(context, TextClockWidgetReceiver::class.java).apply {
                action = UPDATE_TEXT_CLOCK_ACTION
            }
			Handler(Looper.getMainLooper()).postDelayed({
                context.sendBroadcast(updateIntent);
            }, neededTimeToUpdate.toLong() * 1000)
            // context.sendBroadcast(updateIntent)
        }
    }
}
