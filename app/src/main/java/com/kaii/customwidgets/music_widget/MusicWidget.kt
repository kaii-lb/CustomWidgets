package com.kaii.customwidgets.music_widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.PorterDuff
import android.graphics.drawable.Icon
import android.media.AudioManager
import android.media.MediaDescription
import android.media.session.MediaSession.QueueItem
import android.media.session.PlaybackState
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.width
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.unit.ColorProvider
import com.kaii.customwidgets.R
import com.kaii.customwidgets.music_widget.extension_functions.LaunchMediaPlayer
import com.kaii.customwidgets.music_widget.longboi_ui.ImageAndTitle
import com.kaii.customwidgets.music_widget.longboi_ui.UpNextAndControls
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MusicWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    companion object {
       private val TWO_CELLS = DpSize(110.dp, 110.dp)
       private val THREE_CELLS = DpSize(250.dp, 250.dp)
    }
 
    override val sizeMode = SizeMode.Responsive(
        setOf(
            TWO_CELLS, THREE_CELLS
        )
    )
    // override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        actionStartService(Intent(context, NotificationListenerCustomService::class.java))

        provideContent {
            val prefs = currentState<Preferences>()
            val artist = prefs[MusicWidgetReceiver.artist]
            val album = prefs[MusicWidgetReceiver.album]
            val songTitle = prefs[MusicWidgetReceiver.songTitle]
            val length = prefs[MusicWidgetReceiver.length]
            val position = prefs[MusicWidgetReceiver.position]
            val state = prefs[MusicWidgetReceiver.state]
            val albumArt = MusicWidgetReceiver.albumArt
            val queue = MusicWidgetReceiver.queue
            val volume = prefs[MusicWidgetReceiver.volume]
            val maxVolume = prefs[MusicWidgetReceiver.maxVolume]
            val likedYoutubeVideo = prefs[MusicWidgetReceiver.likedYoutubeVideo]

            val playbackState = state ?: PlaybackState.STATE_STOPPED

            GlanceTheme {
                val size = LocalSize.current

                val musicWidgetUIState = MusicWidgetUIState(
                    artist = if (artist == "Not Available") null else artist,
                    album = if (album == "Not Available") null else album,
                    songTitle = if (songTitle == "Not Available") null else songTitle,
                    length = length ?: 0.toLong(),
                    position = position ?: 0.toLong(),
                    albumArt = albumArt,
                    queue = queue,
                    volume = volume ?: 0,
                    maxVolume = maxVolume ?: 100,
                    likedYoutubeVideo = likedYoutubeVideo ?: false,
                )

                if (size.width > TWO_CELLS.width && size.height > TWO_CELLS.height) {
                    LongFormContent(musicWidgetUIState, playbackState)
                } else {
                    ShortFormContent(musicWidgetUIState, playbackState)
                }
            }
        }
    }

    @Composable
    private fun ShortFormContent(musicWidgetUIState: MusicWidgetUIState, playbackState: Int) {
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
                        val icon = NotificationListenerCustomService.statusBarIcon ?: Icon.createWithResource(
                            LocalContext.current, R.drawable.genres)
                        icon.setTintMode(PorterDuff.Mode.SRC_IN)
                        icon.setTintBlendMode(BlendMode.SRC_IN)
                        icon.setTint(Color.White.copy(alpha = 0.6f).toArgb())

	                	// music player icon + launch button
	                   	Image(
	                    	provider = ImageProvider(icon),
	                        contentDescription = "music player",
	                        contentScale = ContentScale.Fit,
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
	                                NotificationListenerCustomService.skipBackward()
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
	                                NotificationListenerCustomService.playPause()
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
                                    NotificationListenerCustomService.skipForward()
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
	        }
	    }
    }

    @Composable
    private fun LongFormContent(musicWidgetUIState: MusicWidgetUIState, playbackState: Int) {
    	Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(8.dp)
                .cornerRadius(16.dp)
                .background(ColorProvider(Color.Transparent)),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        ) {
	        Column(
	            modifier = GlanceModifier
	                .fillMaxSize()
	                .padding(14.dp)
	                .cornerRadius(24.dp)
                    .appWidgetBackground()
	                .background(GlanceTheme.colors.widgetBackground),
	            verticalAlignment = Alignment.Vertical.CenterVertically,
	            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
	        ) {
	            Row(
	                modifier = GlanceModifier
	                    .background(GlanceTheme.colors.widgetBackground)
	                    .fillMaxWidth()
	                    .height(100.dp).cornerRadius(8.dp)
	            ) {
	                ImageAndTitle(musicWidgetUIState)
	            }

	            Spacer(GlanceModifier.height(8.dp))

	            UpNextAndControls(playbackState, musicWidgetUIState)
	        }
        }
    }
}

class MusicWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MusicWidget()

    companion object {
        val artist = stringPreferencesKey("song_artist")
        val album = stringPreferencesKey("song_album")
        val songTitle = stringPreferencesKey("song_title")
        val length = longPreferencesKey("song_length")
        val position = longPreferencesKey("song_position")
        val state = intPreferencesKey("playback_state")
        var albumArt = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
        var queue = List(10) { index ->
            val description = MediaDescription.Builder().setTitle("Not Available").build()

            QueueItem(description, index.toLong())
        }
        val volume = intPreferencesKey("current_volume")
        val maxVolume = intPreferencesKey("max_volume")
        val likedYoutubeVideo = booleanPreferencesKey("liked_youtube_video")

        val isYoutubeKey = ActionParameters.Key<Boolean>("isYoutubeKey")
        val songTitleKey = ActionParameters.Key<String>("songTitleKey")
        val artistKey = ActionParameters.Key<String>("artistKey")
    }

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        //for updates that happen from outside the widget
		getMetadata(context)
        getPlaybackState(context)
        separateGetVolume(context)
		println("UPDATED WIDGET")
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        separateGetVolume(context)

        when (intent.action) {
            MusicWidgetRefreshCallback.UPDATE_ACTION -> {
                getMetadata(context)
            }

            MusicWidgetRefreshCallback.STATE_ACTION -> {
                getPlaybackState(context)
            }

			// idk why this but deal with it later
            MusicWidgetRefreshCallback.VOLUME_ACTION -> {
                getVolume(context)
            }

            NotificationListenerCustomService.NOTIFICATION_LISTENER_CONFIG_CHANGED -> {
                forceUpdate(context)
            }
        }
    }

    private fun getMetadata(context: Context) {
        MainScope().launch {
            val newMusicInfo = NotificationListenerCustomService.updateMetadata()

            println("MUSIC INFO WOOOO: ${newMusicInfo.songTitle}")

            val manager = GlanceAppWidgetManager(context)
            val ids = manager.getGlanceIds(MusicWidget().javaClass)

            ids.forEach { glanceID ->
                glanceID.let {
                    updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                        pref.toMutablePreferences().apply {
                            this[artist] = newMusicInfo.artist ?: "Not Available"
                            this[album] = newMusicInfo.album ?: "Not Available"
                            this[songTitle] = newMusicInfo.songTitle ?: "Not Available"
                            this[length] = newMusicInfo.length
                            this[position] = newMusicInfo.position
                            albumArt = newMusicInfo.albumArt
                            queue = newMusicInfo.queue
                            this[likedYoutubeVideo] = newMusicInfo.likedYoutubeVideo
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
            val ids = manager.getGlanceIds(MusicWidget().javaClass)

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

    private fun getVolume(context: Context) {
        MainScope().launch {
            val currentVolume = NotificationListenerCustomService.volume
            val maximumVolume = NotificationListenerCustomService.maxVolume

            Log.d("MUSIC_WIDGET", "new current volume $currentVolume")

            val manager = GlanceAppWidgetManager(context)
            val ids = manager.getGlanceIds(MusicWidget().javaClass)

            ids.forEach { glanceID ->
                glanceID.let {
                    updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                        pref.toMutablePreferences().apply {
                            this[volume] = currentVolume
                            this[maxVolume] = maximumVolume
                        }
                    }
                    glanceAppWidget.update(context, it)
                }
            }
        }
    }

    private fun separateGetVolume(context: Context) {
        MainScope().launch {
            val am = (context.getSystemService(Context.AUDIO_SERVICE) as AudioManager)
            val currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC)
            val maximumVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

            Log.d("MUSIC_WIDGET", "new current volume $currentVolume")

            val manager = GlanceAppWidgetManager(context)
            val ids = manager.getGlanceIds(MusicWidget().javaClass)

            ids.forEach { glanceID ->
                glanceID.let {
                    updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                        pref.toMutablePreferences().apply {
                            this[volume] = currentVolume
                            this[maxVolume] = maximumVolume
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

            val glanceIDs = manager.getGlanceIds(MusicWidget::class.java)
            glanceIDs.forEach { id ->
                glanceAppWidget.update(context, id)
            }
        }
    }
}



