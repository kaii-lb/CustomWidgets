package com.kaii.customwidgets.music_widget.shortboi_ui

import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider
import com.kaii.customwidgets.music_widget.MusicWidgetUIState

@Composable
fun BackgroundContent(musicWidgetUIState: MusicWidgetUIState, playbackState: Int) {
    Row(
        modifier = GlanceModifier
            .fillMaxSize() //.size(size.width * 1.25f)
            .padding(0.dp)
            .background(ColorProvider(Color.Transparent)),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

        ) {
        val size = LocalSize.current
        val neededSize = if (size.height >= size.width) {
            size.width * 1.45f
        } else {
            size.height * 1.45f
        }
        Column (
            modifier = GlanceModifier
                .size(neededSize - 8.dp)
                .cornerRadius(32.dp)
                .padding(8.dp)
                .appWidgetBackground()
                .background(GlanceTheme.colors.widgetBackground),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

            ) {
            val albumArt = musicWidgetUIState.albumArt
            val emptyBitmap = Bitmap.createBitmap(albumArt.width, albumArt.height, Bitmap.Config.ARGB_8888)

            val backgroundModifier = if (albumArt.sameAs(emptyBitmap)) {
                GlanceModifier.background(GlanceTheme.colors.primaryContainer.getColor(LocalContext.current).copy(alpha = 0.2f))
            } else {
                GlanceModifier.background(ImageProvider(albumArt))
            }

            Column (
                modifier = GlanceModifier
                    .fillMaxSize() // size(size.width * 1.15f)
                    .cornerRadius(24.dp)
                    .then(backgroundModifier),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            ) {
                ShortControls(playbackState)
            }
        }
    }
}