package com.kaii.customwidgets.pill_music_widget

import android.app.ActivityManager
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Canvas
import android.media.AudioManager
import android.media.MediaDescription
import android.media.session.MediaSession
import android.media.session.MediaSession.QueueItem
import android.media.session.PlaybackState
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartService
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.kaii.customwidgets.R
import com.kaii.customwidgets.music_widget.MusicWidget
import com.kaii.customwidgets.music_widget.MusicWidgetRefreshCallback
import com.kaii.customwidgets.music_widget.MusicWidgetUIState
import com.kaii.customwidgets.music_widget.extension_functions.LaunchMediaPlayer
import com.kaii.customwidgets.notification_listener_service.NotificationListenerCustomService
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PillMusicWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override val sizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        actionStartService(Intent(context, NotificationListenerCustomService::class.java))

        provideContent {
            val prefs = currentState<Preferences>()
            val artist = prefs[PillMusicWidgetReceiver.artist]
            val album = prefs[PillMusicWidgetReceiver.album]
            val songTitle = prefs[PillMusicWidgetReceiver.songTitle]
            val state = prefs[PillMusicWidgetReceiver.state]
            val albumArt = PillMusicWidgetReceiver.albumArt
            val queue = PillMusicWidgetReceiver.queue

            val playbackState = state ?: PlaybackState.STATE_STOPPED

            GlanceTheme {
                val musicWidgetUIState = MusicWidgetUIState(
                    artist = if (artist == "Not Available") null else artist,
                    album = if (album == "Not Available") null else album,
                    songTitle = if (songTitle == "Not Available") null else songTitle,
                    length = 0.toLong(),
                    position = 0.toLong(),
                    albumArt = albumArt,
                    queue = queue,
                    volume = 0,
                    maxVolume = 100,
                    likedYoutubeVideo = false,
                )

                SingleRowContent(musicWidgetUIState, playbackState)
            }
        }
    }

    @Composable
    private fun SingleRowContent(musicWidgetUIState: MusicWidgetUIState, playbackState: Int) {
        Column (
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(8.dp)
                .cornerRadius(1000.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {	
            Row (
                modifier = GlanceModifier
                    .appWidgetBackground()
                    .fillMaxSize()
                    .background(GlanceTheme.colors.widgetBackground)
                    .cornerRadius(1000.dp)
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.Start
            ) {
                val size = LocalSize.current
                val neededDimens = 64.dp
	
                Column (
                    modifier = GlanceModifier
                        .size(neededDimens)
                        .cornerRadius(1000.dp)
                        .padding(2.dp)
                        .background(GlanceTheme.colors.primary),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val providedImage = musicWidgetUIState.albumArt
                    val emptyBitmap = Bitmap.createBitmap(providedImage.width, providedImage.height, Bitmap.Config.ARGB_8888)
                    val scale: ContentScale

                    val image: Bitmap = if (providedImage.sameAs(emptyBitmap)) {
                        val drawable = AppCompatResources.getDrawable(LocalContext.current, R.drawable.no_media_playing)
                        val color = GlanceTheme.colors.primary.getColor(LocalContext.current).toArgb()
                        val filter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)

                        val bitmap = Bitmap.createBitmap(drawable?.intrinsicWidth ?: 256, drawable?.intrinsicHeight ?: 256, Bitmap.Config.ARGB_8888)

                        val canvas = Canvas(bitmap)
                        drawable?.setBounds(0, 0, canvas.width, canvas.height)
                        drawable?.colorFilter = filter
                        drawable?.draw(canvas)

                        scale = ContentScale.Fit

                        bitmap
                    } else {
                        scale = ContentScale.Crop
                        providedImage
                    }

                    Image(
                        provider = ImageProvider(image),
                        contentScale = scale,
                        contentDescription = "album art",
                        modifier = GlanceModifier
                            .background(GlanceTheme.colors.widgetBackground)
                            .cornerRadius(1000.dp)
                            .clickable(actionRunCallback(LaunchMediaPlayer::class.java))
                            .fillMaxSize(),
                    )
                }

                Column (
                    modifier = GlanceModifier
                        .fillMaxHeight()
                        .width(size.width / 2)
                        .padding(8.dp, 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.Start
                ) {
                    val lightColor = GlanceTheme.colors.onBackground.getColor(LocalContext.current).copy(alpha = 0.69f)

                    Text (
                        text = musicWidgetUIState.songTitle ?: "Music Stopped",
                        maxLines = 1,
                        style = TextStyle(
                            color = GlanceTheme.colors.onBackground,
                            fontSize = TextUnit(16.0f, TextUnitType.Sp)
                        ),
                    )

                    Text (
                        text = musicWidgetUIState.artist ?: "playback stopped",
                        maxLines = 1,
                        style = TextStyle(
                            color = ColorProvider(lightColor),
                            fontSize = TextUnit(12.0f, TextUnitType.Sp)
                        ),
                        modifier = GlanceModifier.padding(0.dp, (-4).dp, 0.dp, 0.dp)
                    )
                }

                Row (
                    modifier = GlanceModifier
                        .fillMaxHeight()
                        .width(size.width / 2)
                        .padding(0.dp, 0.dp, 8.dp, 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.End
                ) {
                    var playPauseDrawable by remember { mutableIntStateOf(R.drawable.play) }

                    playPauseDrawable = if (playbackState == PlaybackState.STATE_PLAYING) {
                        R.drawable.pause
                    } else {
                        R.drawable.play
                    }

                    Column(
                        modifier = GlanceModifier
                            .size(34.dp)
                            .background(GlanceTheme.colors.secondary)
                            .cornerRadius(16.dp)
                            .clickable(
                                rippleOverride = R.drawable.music_button_ripple
                            ) {
                                Thread.sleep(50)
                                NotificationListenerCustomService.skipBackward()
                                Thread.sleep(50)
                            }
                            .padding(0.dp, 0.dp, 1.dp, 0.dp),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                    ) {
                        // skip backwards button
                        Image(provider = ImageProvider(R.drawable.skip_back),
                            contentDescription = "skip backwards",
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSecondary),
                            modifier = GlanceModifier
                                .defaultWeight()
                                .size(34.dp)
                        )
                    }

                    Spacer (modifier = GlanceModifier.width(4.dp))

                    Column(
                        modifier = GlanceModifier
                            .size(34.dp)
                            .background(GlanceTheme.colors.secondary)
                            .cornerRadius(16.dp)
                            .clickable(
                                rippleOverride = R.drawable.music_button_ripple
                            ) {
                                Thread.sleep(50)
                                // playPauseDrawable = if (playPauseDrawable == R.drawable.pause) R.drawable.play else R.drawable.pause
                                NotificationListenerCustomService.playPause()
                                Thread.sleep(50)
                            }
                            .padding(2.dp, 0.dp, 0.dp, 0.dp),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                    ) {
                        // play pause button
                        Image(provider = ImageProvider(playPauseDrawable),
                            contentDescription = "play pause",
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSecondary),
                            modifier = GlanceModifier
                                .defaultWeight()
                                .size(36.dp)
                        )
                    }

                    Spacer (modifier = GlanceModifier.width(4.dp))

                    Column(
                        modifier = GlanceModifier
                            .size(34.dp)
                            .background(GlanceTheme.colors.secondary)
                            .cornerRadius(16.dp)
                            .clickable(
                                rippleOverride = R.drawable.music_button_ripple
                            ) {
                                Thread.sleep(50)
                                NotificationListenerCustomService.skipForward()
                                Thread.sleep(50)
                            }
                            .padding(1.dp, 0.dp, 0.dp, 0.dp),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
                    ) {
                        // skip forward button
                        Image(provider = ImageProvider(R.drawable.skip_ahead),
                            contentDescription = "skip forwards",
                            contentScale = ContentScale.Fit,
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSecondary),
                            modifier = GlanceModifier
                                .defaultWeight()
                                .size(34.dp)
                        )
                    }
                }
            }
        }
    }
}

class PillMusicWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PillMusicWidget()

    companion object {
        val artist = stringPreferencesKey("song_artist")
        val album = stringPreferencesKey("song_album")
        val songTitle = stringPreferencesKey("song_title")
        val state = intPreferencesKey("playback_state")
        var albumArt = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
        var queue = List(0) { index ->
            val description = MediaDescription.Builder().setTitle("Not Available").build()

            QueueItem(description, index.toLong())
        }
    }

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        // for updates that happen from outside the widget
        getMetadata(context)
        getPlaybackState(context)
        println("UPDATED WIDGET")
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            MusicWidgetRefreshCallback.UPDATE_ACTION -> {
                getMetadata(context)
            }

            MusicWidgetRefreshCallback.STATE_ACTION -> {
                getPlaybackState(context)
            }

            NotificationListenerCustomService.NOTIFICATION_LISTENER_CONFIG_CHANGED -> {
                forceUpdate(context)
            }
        }
    }

    private fun getMetadata(context: Context) {
        MainScope().launch {
            val newMusicInfo = NotificationListenerCustomService.updateMetadata()

            println("PILL MUSIC INFO WOOOO: ${newMusicInfo.songTitle}")

            val manager = GlanceAppWidgetManager(context)
            val ids = manager.getGlanceIds(PillMusicWidget::class.java)

            ids.forEach { glanceID ->
                glanceID.let {
                    updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                        pref.toMutablePreferences().apply {
                            this[artist] = newMusicInfo.artist ?: "Not Available"
                            this[album] = newMusicInfo.album ?: "Not Available"
                            this[songTitle] = newMusicInfo.songTitle ?: "Not Available"
                            albumArt = newMusicInfo.albumArt
                            queue = newMusicInfo.queue
                        }
                    }
                    glanceAppWidget.update(context, it)
                }
            }
        }
    }

    private fun getPlaybackState(context: Context) {
        MainScope().launch {
            val playbackState = NotificationListenerCustomService.playbackState

            val manager = GlanceAppWidgetManager(context)
            val ids = manager.getGlanceIds(PillMusicWidget::class.java)

            ids.forEach { glanceID ->
                glanceID.let {
                    updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                        pref.toMutablePreferences().apply {
                            this[state] = playbackState
                        }
                    }
                    glanceAppWidget.update(context, it)
                }
            }
        }
    }

    private fun forceUpdate(context: Context) {
        MainScope().launch {
            val manager = GlanceAppWidgetManager(context)

            val glanceIDs = manager.getGlanceIds(PillMusicWidget::class.java)
            glanceIDs.forEach { id ->
                glanceAppWidget.update(context, id)
            }
        }
    }
}
