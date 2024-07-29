package com.kaii.customwidgets.music_widget.shortboi_ui

import android.graphics.BlendMode
import android.graphics.PorterDuff
import android.graphics.drawable.Icon
import android.media.session.PlaybackState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import com.kaii.customwidgets.R
import com.kaii.customwidgets.music_widget.extension_functions.LaunchMediaPlayer
import com.kaii.customwidgets.notification_listener_service.NotificationListenerCustomService

@Composable
fun ShortControls(playbackState: Int) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(4.dp, 4.dp, 8.dp, 0.dp)
            .clickable(
                rippleOverride = R.drawable.music_button_ripple,
                onClick = actionRunCallback(LaunchMediaPlayer::class.java)
            ),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.End,
    ) {


        val icon: Icon
        val scale: ContentScale
        if (NotificationListenerCustomService.statusBarIcon != null) {
            icon = NotificationListenerCustomService.statusBarIcon ?: Icon.createWithResource(
                LocalContext.current, R.drawable.genres)
            scale = ContentScale.Fit
            icon.setTint(Color.White.copy(alpha = 0.6f).toArgb())
        }
        else {
            icon = Icon.createWithResource(LocalContext.current, R.drawable.genres)
            scale = ContentScale.Crop
        }

        icon.setTintMode(PorterDuff.Mode.SRC_IN)
        icon.setTintBlendMode(BlendMode.SRC_IN)

        // music player icon + launch button
        Image(
            provider = ImageProvider(icon),
            contentDescription = "music player",
            contentScale = scale,
            modifier = GlanceModifier.height(18.dp) // 26.dp
        )
    }
    var playPauseDrawable by remember { mutableIntStateOf(R.drawable.play) }

    playPauseDrawable = if (playbackState == PlaybackState.STATE_PLAYING) {
        R.drawable.pause
    } else {
        R.drawable.play
    }

    Row (
        modifier = GlanceModifier
            .fillMaxSize() // size(size.width * 1.15f)
            .cornerRadius(24.dp),
        verticalAlignment = Alignment.Vertical.CenterVertically,
        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxHeight()
                .width(50.dp)
                .clickable(
                    rippleOverride = R.drawable.music_button_ripple
                ) {
                    Thread.sleep(50)
                    NotificationListenerCustomService.skipBackward()
                    Thread.sleep(50)
                }
                .padding((2).dp, 22.dp, 0.dp, 40.dp), // size(size.width * 1.15f),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        ) {
            // skip backwards button
            Image(provider = ImageProvider(R.drawable.skip_back),
                contentDescription = "skip backwards",
                contentScale = ContentScale.Fit,
                modifier = GlanceModifier
                    .defaultWeight()
                    .size(40.dp)
            )
        }

        Column(
            modifier = GlanceModifier
                .fillMaxHeight()
                .width(50.dp)
                .clickable(
                    rippleOverride = R.drawable.music_button_ripple
                ) {
                    Thread.sleep(50)
                    NotificationListenerCustomService.playPause()
                    Thread.sleep(50)
                }
                .padding(0.dp, 22.dp, 0.dp, 40.dp), // size(size.width * 1.15f),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        ) {

            // play pause button
            Image(provider = ImageProvider(playPauseDrawable),
                contentDescription = "play pause",
                contentScale = ContentScale.Fit,
                modifier = GlanceModifier
                    .defaultWeight()
                    .size(42.dp)
            )
        }

        Column(
            modifier = GlanceModifier
                .fillMaxHeight()
                .width(50.dp)
                .clickable(
                    rippleOverride = R.drawable.music_button_ripple
                ) {
                    Thread.sleep(50)
                    NotificationListenerCustomService.skipForward()
                    Thread.sleep(50)
                }
                .padding(0.dp, 22.dp, 4.dp, 40.dp), // size(size.width * 1.15f),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        ) {
            // skip forward button
            Image(provider = ImageProvider(R.drawable.skip_ahead),
                contentDescription = "skip forwards",
                contentScale = ContentScale.Fit,
                modifier = GlanceModifier
                    .defaultWeight()
                    .size(40.dp)
            )
        }
    }
}
