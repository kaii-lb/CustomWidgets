package com.kaii.customwidgets.music_widget.longboi_ui

import android.content.res.ColorStateList
import android.media.session.PlaybackState
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.kaii.customwidgets.R
import com.kaii.customwidgets.music_widget.MusicWidgetReceiver
import com.kaii.customwidgets.music_widget.MusicWidgetUIState
import com.kaii.customwidgets.music_widget.NotificationListenerCustomService
import com.kaii.customwidgets.music_widget.extension_functions.GetLyricsForSong

@Composable
fun UpNextAndControls(playbackState: Int, musicWidgetUIState: MusicWidgetUIState) {
    Row (
        GlanceModifier
            .background(GlanceTheme.colors.widgetBackground)
            .fillMaxWidth()
            .cornerRadius(8.dp)
    ) {
        Column (
            GlanceModifier
                .background(GlanceTheme.colors.widgetBackground)
                .defaultWeight()
                .fillMaxHeight()
                .cornerRadius(8.dp)
        ) {
            Column (
                GlanceModifier
                    .background(GlanceTheme.colors.widgetBackground)
                    .defaultWeight()
                    .fillMaxHeight()
                    .cornerRadius(8.dp)
            ) {
                Column (
                    GlanceModifier
                        .background(GlanceTheme.colors.widgetBackground)
                        .defaultWeight()
                        .fillMaxWidth()
                        .cornerRadius(8.dp)
                ) {
                    Column (
                        GlanceModifier
                            .background(GlanceTheme.colors.widgetBackground)
                            .defaultWeight()
                            .fillMaxWidth()
                            .cornerRadius(8.dp)
                    ) {
                        Column (
                            GlanceModifier
                                .background(GlanceTheme.colors.secondaryContainer)
                                .defaultWeight()
                                .fillMaxWidth()
                                .padding(4.dp)
                                .cornerRadius(8.dp)
                        ) {
                            val queue = musicWidgetUIState.queue

//                                queue.forEach { item ->
//                                    println("QUEUE ITEM IS: ${item.description?.title}")
//                                }

                            // TODO: fix so it shows less than 3 items if not everything is available
                            if (queue.size >= 4) {
                                val firstItem = queue[1]
                                val secondItem = queue[2]
                                val thirdItem = queue[3]

                                val firstItemTitle = firstItem.description.title ?: "Not Available"
                                val secondItemTitle = secondItem.description.title ?: "Not Available"
                                val thirdItemTitle = thirdItem.description.title ?: "Not Available"

                                val glanceColor = GlanceTheme.colors.onBackground.getColor(
                                    LocalContext.current).toArgb()
                                val lightColor = Color(glanceColor.red, glanceColor.green, glanceColor.blue, 176)

                                Text(text = "NEXT UP", style = TextStyle(fontWeight = FontWeight.Bold, color = ColorProvider(lightColor)))

                                Text(text = "-> $firstItemTitle",
                                    maxLines = 1,
                                    modifier = GlanceModifier.clickable {
                                        NotificationListenerCustomService.playSongFromQueue(firstItem.queueId)
                                    },
                                    style = TextStyle(color = GlanceTheme.colors.onSecondaryContainer)
                                )

                                Text(text = "-> $secondItemTitle",
                                    maxLines = 1,
                                    modifier = GlanceModifier.clickable {
                                        NotificationListenerCustomService.playSongFromQueue(secondItem.queueId)
                                    },
                                    style = TextStyle(color = GlanceTheme.colors.onSecondaryContainer)
                                )

                                Text(text = "-> $thirdItemTitle",
                                    maxLines = 1,
                                    modifier = GlanceModifier.clickable {
                                        NotificationListenerCustomService.playSongFromQueue(thirdItem.queueId)
                                    },
                                    style = TextStyle(color = GlanceTheme.colors.onSecondaryContainer)
                                )
                            } else {
                                val glanceColor = GlanceTheme.colors.onBackground.getColor(
                                    LocalContext.current).toArgb()
                                val lightColor = Color(glanceColor.red, glanceColor.green, glanceColor.blue, 176)

                                Text(text = "NEXT UP", style = TextStyle(fontWeight = FontWeight.Bold, color = ColorProvider(lightColor)))

                                Text(text = "-> Not Available",maxLines = 1, style = TextStyle(fontWeight = FontWeight.Normal, color = GlanceTheme.colors.onSecondaryContainer))
                                Text(text = "-> Not Available", maxLines = 1, style = TextStyle(fontWeight = FontWeight.Normal, color = GlanceTheme.colors.onSecondaryContainer))
                                Text(text = "-> Not Available", maxLines = 1, style = TextStyle(fontWeight = FontWeight.Normal, color = GlanceTheme.colors.onSecondaryContainer))
                            }
                        }
                    }
                }

                Spacer(GlanceModifier.height(8.dp))

                SetupButtonControls(playbackState, musicWidgetUIState)
            }
        }

        Spacer(GlanceModifier.width(12.dp))

        Column (
            GlanceModifier
                .background(GlanceTheme.colors.inverseOnSurface)
                .width(40.dp)
                .fillMaxHeight()
                .cornerRadius(8.dp),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            //implement slider
            val packageName = LocalContext.current.packageName
            val remoteViews = RemoteViews(packageName, R.layout.music_widget_volume_slider_layout)

            remoteViews.setProgressBar(R.id.volume_slider, musicWidgetUIState.maxVolume, musicWidgetUIState.volume, false)

            remoteViews.setColorStateList(
                R.id.volume_slider,
                "setProgressTintList",
                ColorStateList.valueOf(
                    GlanceTheme.colors.primary.getColor(LocalContext.current).toArgb()
                )
            )

            AndroidRemoteViews(
                remoteViews = remoteViews,
                modifier = GlanceModifier.defaultWeight()
            )
        }
    }
}

@Composable
private fun SetupButtonControls(playbackState: Int, musicWidgetUIState: MusicWidgetUIState) {
    Row (
        modifier = GlanceModifier
            .background(GlanceTheme.colors.widgetBackground)
            .height(40.dp)
            .fillMaxWidth()
            .cornerRadius(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            text = if (playbackState == PlaybackState.STATE_PLAYING) {
                "||"
            } else {
                "|>"
            },
            onClick = {
                NotificationListenerCustomService.playPause()
            },
            modifier = GlanceModifier
                .width(64.dp)
                .cornerRadius(2.dp)
                .fillMaxHeight(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = GlanceTheme.colors.primary
            )
        )
        Spacer(GlanceModifier.defaultWeight())

        Button(
            text = "<<",
            onClick = {
                NotificationListenerCustomService.skipBackward()
            },
            modifier = GlanceModifier
                .width(40.dp)
                .padding((-10).dp)
                .cornerRadius(32.dp)
                .fillMaxHeight(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = GlanceTheme.colors.secondary,
                contentColor = GlanceTheme.colors.surface
            )
        )
        Spacer(GlanceModifier.defaultWeight())

        Button(
            text = ">>",
            onClick = {
                NotificationListenerCustomService.skipForward()
            },
            modifier = GlanceModifier
                .width(40.dp)
                .padding((-10).dp)
                .cornerRadius(32.dp)
                .fillMaxHeight(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = GlanceTheme.colors.secondary,
                contentColor = GlanceTheme.colors.surface
            )
        )
        Spacer(GlanceModifier.defaultWeight())

        val isYoutube = NotificationListenerCustomService.isYoutube()
        val enabled: Boolean

        val buttonText = if (isYoutube && musicWidgetUIState.likedYoutubeVideo) {
            enabled = false
            "Liked!"
        } else if (isYoutube) {
            enabled = true
            "Like"
        } else {
            enabled = true
            "Lyrics"
        }

        Button(
            text = buttonText,
            onClick = actionRunCallback(
                GetLyricsForSong::class.java, actionParametersOf(
                    MusicWidgetReceiver.isYoutubeKey to isYoutube,
                    MusicWidgetReceiver.songTitleKey to (musicWidgetUIState.songTitle ?: "this is definitely most like absolutely not a song title lololol"),
                    MusicWidgetReceiver.artistKey to (musicWidgetUIState.artist ?: "this is definitely most like absolutely not an artist lololol")
                )
            ),
            modifier = GlanceModifier
                .width(70.dp)
                .fillMaxHeight()
                .cornerRadius(0.dp)
                .padding((-10).dp),
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = GlanceTheme.colors.secondary,
                contentColor = GlanceTheme.colors.surface
            )
        )
    }
}
