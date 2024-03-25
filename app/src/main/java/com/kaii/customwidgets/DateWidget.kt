@file:Suppress("SpellCheckingInspection")

package com.kaii.customwidgets

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import fuel.Fuel
import fuel.get
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateWidget : GlanceAppWidget() {
    companion object {
        private val THREE_CELLS = DpSize(240.dp, 160.dp)
        private val TWO_CELLS = DpSize(160.dp, 160.dp)
    }

    override val sizeMode = SizeMode.Responsive (
        setOf (
            TWO_CELLS,
            THREE_CELLS
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                ShowDateWidget()
            }
        }
    }

    @Composable
    fun ShowDateWidget() {
        val size = LocalSize.current
        Log.d("UNIQUETAG", size.width.toString())

        Column (
            modifier = GlanceModifier
                .fillMaxSize(),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        ) {
            Column (
                modifier = GlanceModifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            ) {
                ShowDate(size.width.value)
            }

            Spacer(
                modifier = GlanceModifier
                    .height(24.dp)
            )

            Row (
                modifier = GlanceModifier
//                    .background(GlanceTheme.colors.onSurface)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.Start,
            ) {
                val weatherIntent = Intent(
                    Intent.ACTION_MAIN,
                )
                weatherIntent.setComponent(ComponentName.unflattenFromString("com.google.android.googlequicksearchbox/com.google.android.apps.search.weather.WeatherExportedActivity"))

                Column (
                    modifier = GlanceModifier
                        .size(68.dp)
                        .background(GlanceTheme.colors.inverseOnSurface)
                        .clickable(actionStartActivity(weatherIntent))
                        .cornerRadius(100.dp),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                ) {
                    GetWeather()
                }

                Spacer(
                    GlanceModifier
                        .defaultWeight()
                )

                if (size.width > TWO_CELLS.width) {
                    Column (
                        modifier = GlanceModifier
                            .size(68.dp)
                            .background(GlanceTheme.colors.inverseOnSurface)
                            .cornerRadius(100.dp),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                    ) {
                        Text(
                            text = "Alarm",
                            modifier = GlanceModifier
                                .padding(0.dp, 0.dp, 0.dp, 4.dp),
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface,
                                textAlign = TextAlign.Center,
                                fontFamily = FontFamily("ndot57"),
                                fontSize = TextUnit(18.0F, TextUnitType.Sp),
                                fontWeight = FontWeight.Medium
                            )
                        )

                    }
                }

                Spacer(
                    GlanceModifier
                        .defaultWeight()
                )

                val spotifyIntent = Intent(
                    Intent.ACTION_MAIN,
                )
                spotifyIntent.setComponent(ComponentName.unflattenFromString("com.spotify.music/com.spotify.music.MainActivity"))

                Column (
                    modifier = GlanceModifier
                        .size(68.dp)
                        .background(GlanceTheme.colors.inverseOnSurface)
                        .clickable(actionStartActivity(spotifyIntent))
                        .cornerRadius(100.dp),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                ) {
                    Image(
                        provider = ImageProvider(
                            R.drawable.spotify_icon
                        ),
                        contentDescription = null,
                        modifier = GlanceModifier
                            .padding(5.dp),
                        colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface)
                    )
                }
            }
        }
    }

    @Composable
    fun ShowDate(size: Float) {
        val format = DateTimeFormatter.ofPattern("EEEE MMM dd")
        val date = LocalDate.now().format(format)

        val twosize = LocalSize.current
        val spotifyIntent = Intent(
            Intent.ACTION_VIEW,
            CalendarContract.CONTENT_URI.buildUpon().appendPath("time").build()
        )

        var padding = if (size <= 160) {
            20.dp
        }
        else {
            14.dp
        }

        val textSize = when (date.length) {
            16 -> {
                padding += 1.dp
                (twosize.width.value / 10.25F)
            }
            15 -> {
                (twosize.width.value / 10.0F)
            }
            13, 14 -> {
                padding -= 1.dp
                (twosize.width.value / 9.0F)
            }
            else -> {
                (twosize.width.value / 10.0F)
            }
        }

        Text(text = date,
            modifier = GlanceModifier
                .background(GlanceTheme.colors.inverseOnSurface)
                .cornerRadius(16.dp)
                .fillMaxWidth()
                .height(64.dp)
                .clickable(actionStartActivity(spotifyIntent))
                .padding(8.dp, padding, 8.dp, 21.dp),
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontFamily = FontFamily("ndot57"),
                textAlign = TextAlign.Center,
                fontSize = TextUnit(textSize, TextUnitType.Sp),
                fontWeight = FontWeight.Medium,
            ),
            maxLines = 1
        )
    }

    @Composable
    private fun GetWeather() {

//        val weatherInfo: String
//        runBlocking {
//            weatherInfo = Fuel.get("https://wttr.in/Salima+Lebanon?format=%t").body
//        }
//        val temp = weatherInfo.filter { it.isDigit() } + "^C"

        Text(
            text = "11^C",
            modifier = GlanceModifier
                .padding(0.dp, 0.dp, 0.dp, 4.dp),
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                textAlign = TextAlign.Center,
                fontFamily = FontFamily("ndot57"),
                fontSize = TextUnit(18.0F, TextUnitType.Sp)
            )
        )
    }
}

class DateWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DateWidget()
}