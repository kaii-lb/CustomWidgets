package com.kaii.customwidgets

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontFamily
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateWidget : GlanceAppWidget() {

    companion object {
        private val THREE_CELLS = DpSize(240.dp, 240.dp)
        private val TWO_CELLS = DpSize(160.dp, 240.dp)
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

        Column (
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(0.dp, 0.dp, 0.dp, 0.dp),
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
                    .height(16.dp)
            )

            Row (
                modifier = GlanceModifier
//                    .background(GlanceTheme.colors.onSurface)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.Start,
            ) {
                Column (
                    modifier = GlanceModifier
                        .size(68.dp)
                        .background(GlanceTheme.colors.inverseOnSurface)
                        .cornerRadius(100.dp),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                ) {
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
                            text = "IF YOU",
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

                Spacer(
                    GlanceModifier
                        .defaultWeight()
                )

                Column (
                    modifier = GlanceModifier
                        .size(68.dp)
                        .background(GlanceTheme.colors.inverseOnSurface)
                        .cornerRadius(100.dp),
                    verticalAlignment = Alignment.Vertical.CenterVertically,
                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                ) {
                    Text(
                        text = "TRY",
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
        }
    }

    @Composable
    fun ShowDate(size: Float) {
        val format = DateTimeFormatter.ofPattern("EEEE MMM dd")
        val date = LocalDate.now().format(format)

        val padding = if (size <= 160) {
            18.dp
        }
        else {
            10.dp
        }

        Text(text = date,
            modifier = GlanceModifier
                .background(GlanceTheme.colors.inverseOnSurface)
                .cornerRadius(16.dp)
                .fillMaxWidth()
                .height(64.dp)
                .padding(8.dp, padding, 8.dp, 21.dp),
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontFamily = FontFamily("ndot57"),
                textAlign = TextAlign.Center,
                fontSize = TextUnit(size / 8.0F, TextUnitType.Sp),
                fontWeight = FontWeight.Medium,
            ),
            maxLines = 1
        )
    }
}

class DateWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DateWidget()
}

//class TestActionCallback : ActionCallback {
//    override suspend fun onAction(
//        context: Context,
//        glanceId: GlanceId,
//        parameters: ActionParameters
//    ) {
//        DateWidget.update(context, glanceId)
//    }
//}