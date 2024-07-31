package com.kaii.customwidgets.text_date_widget

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.compose.ui.graphics.Color
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
import androidx.glance.layout.width
import androidx.glance.layout.padding
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontStyle
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date

class TextDateWidget : GlanceAppWidget() {
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
            val updateIntent = Intent(context, TextDateWidgetReceiver::class.java).apply {
                action = TextDateWidgetReceiver.UPDATE_TEXT_CLOCK_ACTION
            }
            context.sendBroadcast(updateIntent)
            alreadyUpdated = true
        }

        provideContent {
            GlanceTheme {
                val size = LocalSize.current

                if (size.width == TWO_ONE.width && size.height == TWO_ONE.height) {
                    HorizontalContent(28f, 1.5f, true)
                }
                else if (size.width <= ONE_TWO.width && size.height <= ONE_TWO.height) {
                    VerticalContent(24f, 0.7f) // make it true vertical
                }
                else if (size.width <= TWO_TWO.width && size.height <= TWO_TWO.height) {
                    VerticalContent(36f, 1.25f)
                }
                else if (size.width <= THREE_TWO.width && size.height <= THREE_TWO.height) {
                    HorizontalContent(48f, 1.5f)
                }
                else if (size.width == FOUR_THREE.width && size.height >= FOUR_THREE.height) {
                    HorizontalContent(56f, 1.6f)
                }
                else if (size.width >= THREE_TWO.width && size.height <= THREE_TWO.height) {
                    VerticalContent(72f, 1.5f)
                }
                else {
                    VerticalContent(72f, 1f)
                }
            }
        }
    }

    @Composable
    private fun HorizontalContent(textSize: Float, scale: Float, short: Boolean = false) {
    	val size = LocalSize.current

    	val shortModifier = if (short) {
    		GlanceModifier.height(size.height * 1.75f)
    	}
    	else {
    		GlanceModifier.fillMaxSize()
    	}
    	Row (
            modifier = GlanceModifier
                .background(ColorProvider(Color.Transparent))
                .cornerRadius(48.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
	        Row (
	            modifier = GlanceModifier
	                .appWidgetBackground()
	                .background(GlanceTheme.colors.widgetBackground)
	                .cornerRadius(48.dp)
	                .fillMaxWidth()
	                .then(shortModifier),
	            verticalAlignment = Alignment.CenterVertically,
	            horizontalAlignment = Alignment.CenterHorizontally
	        ) {
	        	val size = LocalSize.current
	            val dayFormat = DateTimeFormatter.ofPattern("EE")
	            val dateFormat = DateTimeFormatter.ofPattern("dd")

	            val now = LocalDate.now()
	            val day by remember { mutableStateOf( now.format(dayFormat) ) }
	            val date by remember { mutableStateOf( now.format(dateFormat) ) }

	            Text(
	                text = day,
	                maxLines = 1,
	                style = TextStyle(
	                    textAlign = TextAlign.Center,
	                    color = GlanceTheme.colors.onBackground,
	                    fontWeight = FontWeight.Bold,
	                    fontSize = TextUnit(textSize, TextUnitType.Sp),
	                )
	            )

	            Spacer(modifier = GlanceModifier.width(8.dp))

	            Text(
	                text = date,
	                maxLines = 1,
	                style = TextStyle(
	                    textAlign = TextAlign.Center,
	                    color = GlanceTheme.colors.onPrimary,
	                    fontWeight = FontWeight.Bold,
	                    fontSize = TextUnit(textSize, TextUnitType.Sp),
	                ),
	                modifier = GlanceModifier
	                    .background(GlanceTheme.colors.primary)
	                    .cornerRadius(32.dp)
	                    .width(size.width / scale)
	            )
	        }
        }
    }

    @Composable
    private fun VerticalContent(textSize: Float, scale: Float) {
        Column (
            modifier = GlanceModifier
                .appWidgetBackground()
                .background(GlanceTheme.colors.widgetBackground)
                .cornerRadius(48.dp)
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        	val size = LocalSize.current
            val dayFormat = DateTimeFormatter.ofPattern("EEE")
            val dateFormat = DateTimeFormatter.ofPattern("dd")

            val now = LocalDate.now()
            val day by remember { mutableStateOf( now.format(dayFormat) ) }
            val date by remember { mutableStateOf( now.format(dateFormat) ) }

            Text(
                text = day,
                maxLines = 1,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = GlanceTheme.colors.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(textSize, TextUnitType.Sp),
                )
            )

            Spacer(modifier = GlanceModifier.height(16.dp))

            Text(
                text = date,
                maxLines = 1,
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    color = GlanceTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(textSize, TextUnitType.Sp),
                ),
                modifier = GlanceModifier
                    .background(GlanceTheme.colors.primary)
                    .cornerRadius(24.dp)
                    .width(size.width / scale)
            )
        }
    }
}

class TextDateWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TextDateWidget()

    companion object {
        const val UPDATE_TEXT_CLOCK_ACTION = "com.kaii.customwidgets.text_clock_widget.UPDATE_TEXT_CLOCK_ACTION"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == UPDATE_TEXT_CLOCK_ACTION) {
            val now = LocalTime.now()

            val nextTick = now.plusHours(1).withMinute(0).withSecond(0).withNano(0)
            var neededTimeToUpdate = Duration.between(now, nextTick).toMillis()

            if (neededTimeToUpdate <= 0) {
                neededTimeToUpdate = 1000
            }

            println("NEEDED TIME TO UPDATE $neededTimeToUpdate")

            MainScope().launch {
                val manager = GlanceAppWidgetManager(context)
                val ids = manager.getGlanceIds(TextDateWidget::class.java)

                ids.forEach { glanceID ->
                    glanceID.let {
                        glanceAppWidget.update(context, it)
                    }
                }
            }

            // Thread.sleep(neededTimeToUpdate.toLong() * 1000)

            val updateIntent = Intent(context, TextDateWidgetReceiver::class.java).apply {
                action = UPDATE_TEXT_CLOCK_ACTION
            }
            Handler(Looper.getMainLooper()).postDelayed({
                context.sendBroadcast(updateIntent);
            }, neededTimeToUpdate)
            // context.sendBroadcast(updateIntent)
        }
    }
}
