package com.kaii.customwidgets.musicwidget

import android.app.SearchManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.media.MediaDescription
import android.media.session.MediaSession.QueueItem
import android.media.session.PlaybackState
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartService
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
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.kaii.customwidgets.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MusicWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

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

            val musicWidgetUIState = MusicWidgetUIState (
                artist = if(artist == "Unknown Artist") null else artist,
                album = if(album == "Unknown Album") null else album,
                songTitle = if(songTitle == "Unknown Title") null else songTitle,
                length = length ?: 0.toLong(),
                position = position ?: 0.toLong(),
                albumArt = albumArt,
                queue = queue,
                volume = volume ?: 0,
                maxVolume = maxVolume ?: 100,
                likedYoutubeVideo = likedYoutubeVideo ?: false
            )

            val playbackState = state ?: PlaybackState.STATE_STOPPED

            GlanceTheme {
                Content(musicWidgetUIState, playbackState)
            }
        }
    }

    @Composable
    private fun Content(musicWidgetUIState: MusicWidgetUIState, playbackState: Int) {
        Column (
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(14.dp)
                .cornerRadius(16.dp)
                .background(GlanceTheme.colors.widgetBackground),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        ) {
            Row (
                GlanceModifier
                    .background(GlanceTheme.colors.widgetBackground)
                    .fillMaxWidth()
                    .height(100.dp)
                    .cornerRadius(8.dp)
            ) {
                ImageAndTitle(musicWidgetUIState)
            }

            Spacer(GlanceModifier.height(8.dp))

            UpNextAndControls(playbackState, musicWidgetUIState)
        }
    }

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

									val glanceColor = GlanceTheme.colors.onBackground.getColor(LocalContext.current).toArgb()
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
                                	val glanceColor = GlanceTheme.colors.onBackground.getColor(LocalContext.current).toArgb()
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
                    .background(GlanceTheme.colors.widgetBackground)
                    .width(40.dp)
                    .fillMaxHeight()
                    .cornerRadius(8.dp),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
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

                AndroidRemoteViews(remoteViews)
            }
        }
    }

    @Composable
    fun ImageAndTitle(musicWidgetUIState: MusicWidgetUIState) {
        Row (
            GlanceModifier
                .fillMaxSize()
        )
        {
            println("ALBUM ART URI IS ${musicWidgetUIState.albumArt}")

            Column (
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
                    .width(110.dp)
                    .background(Color.Transparent),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column (
                    modifier = GlanceModifier
                        .background(GlanceTheme.colors.primary)
                        .cornerRadius(1000.dp)
                        .size(100.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image (
                        provider = ImageProvider(musicWidgetUIState.albumArt),
                        contentDescription = "Song's Album Art",
                        modifier = GlanceModifier
                            .background(Color.Black)
                            .cornerRadius(1000.dp)
                            .size(96.dp)
                            .clickable(actionRunCallback(OpenMediaPlayerActivity::class.java)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column (
                modifier = GlanceModifier
                    .defaultWeight()
                    .fillMaxHeight()
                    .background(GlanceTheme.colors.widgetBackground)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = musicWidgetUIState.songTitle ?: "Unknown Title",
                    maxLines = 1,
                    style = TextStyle(color = GlanceTheme.colors.onBackground, fontSize = TextUnit(16.0f, TextUnitType.Sp))
                )

                val glanceColor = GlanceTheme.colors.onBackground.getColor(LocalContext.current).toArgb()
                val lightColor = Color(glanceColor.red, glanceColor.green, glanceColor.blue, 176)
                Text(
                    text = musicWidgetUIState.artist ?: "Unknown Artist",
                    maxLines = 1,
                    style = TextStyle(
                        color = ColorProvider(lightColor),
                        fontSize = TextUnit(16.0f, TextUnitType.Sp)
                    )
                )
            }

			Column (
				modifier = GlanceModifier
					.fillMaxHeight()
                    .width(40.dp)
                    .padding((-15).dp)
                   	.background(GlanceTheme.colors.widgetBackground)
                   	.clickable(actionRunCallback(OpenMediaPlayerActivity::class.java)),
           	    verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
			) {
				val packageName = LocalContext.current.packageName
                val remoteViews = RemoteViews(packageName, R.layout.music_player_vertical_text_layout)

                val mediaPlayerPackageName = NotificationListenerCustomService.getMediaPlayer()
                val mediaPlayerTitle: String
                val pm = LocalContext.current.packageManager

                if (mediaPlayerPackageName != null) {
                    val appInfo = pm.getApplicationInfo(mediaPlayerPackageName, 0)
                    mediaPlayerTitle = pm.getApplicationLabel(appInfo).toString()
                }
                else {
                    mediaPlayerTitle = ""
                }

                remoteViews.setTextViewText(
                    R.id.music_player_text,
                    mediaPlayerTitle.uppercase()
                )

                remoteViews.setTextColor(
                    R.id.music_player_text,
                    GlanceTheme.colors.primary.getColor(LocalContext.current).toArgb()
                )

                AndroidRemoteViews(remoteViews)
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
                    .cornerRadius(2.dp)
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
                onClick = actionRunCallback(GrabSongLyricsOrLike::class.java, actionParametersOf(
                    MusicWidgetReceiver.isYoutubeKey to isYoutube,
                    MusicWidgetReceiver.songTitleKey to (musicWidgetUIState.songTitle ?: "this is definitely most like absolutely not a song title lololol"),
                    MusicWidgetReceiver.artistKey to (musicWidgetUIState.artist ?: "this is definitely most like absolutely not an artist lololol")
                )),
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
        var queue = List(3) { index ->
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
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        getMetadata(context)
        getPlaybackState(context)
        println("UPDATED WIDGET")
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == MusicWidgetRefreshCallback.UPDATE_ACTION) {
            getMetadata(context)
        }
        else if (intent.action == MusicWidgetRefreshCallback.STATE_ACTION) {
            getPlaybackState(context)
        }

        when (intent.action) {
            MusicWidgetRefreshCallback.UPDATE_ACTION -> {
                getMetadata(context)
            }
            MusicWidgetRefreshCallback.STATE_ACTION -> {
                getPlaybackState(context)
            }
            MusicWidgetRefreshCallback.VOLUME_ACTION -> {
                getVolume(context)
            }
        }
    }

    private fun getMetadata(context: Context) {
        MainScope().launch {
            val newMusicInfo = NotificationListenerCustomService.updateMetadata()

            println("MUSIC INFO WOOOO: ${newMusicInfo.songTitle}")

            val glanceID = GlanceAppWidgetManager(context).getGlanceIds(MusicWidget::class.java).firstOrNull()

            glanceID?.let {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                    pref.toMutablePreferences().apply {
                        this[artist] = newMusicInfo.artist ?: "Unknown Artist"
                        this[album] = newMusicInfo.album ?: "Unknown Album"
                        this[songTitle] = newMusicInfo.songTitle ?: "Unknown Title"
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

    private fun getPlaybackState(context: Context) {
        MainScope().launch {
            val playbackState = NotificationListenerCustomService.playbackState

            val glanceID =
                GlanceAppWidgetManager(context).getGlanceIds(MusicWidget::class.java).firstOrNull()

            glanceID?.let {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                    pref.toMutablePreferences().apply {
                        this[state] = playbackState
                    }
                }
                glanceAppWidget.update(context, it)
            }
        }
    }

    private fun getVolume(context: Context) {
        MainScope().launch {
            val currentVolume = NotificationListenerCustomService.volume
            val maximumVolume = NotificationListenerCustomService.maxVolume

            val glanceID =
                GlanceAppWidgetManager(context).getGlanceIds(MusicWidget::class.java).firstOrNull()

            glanceID?.let {
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

class OpenMediaPlayerActivity : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val packageName = NotificationListenerCustomService.getMediaPlayer()

        if (packageName != null) {
            println("PACKAGE NAME: $packageName")

            val newIntent = Intent(Intent.ACTION_MAIN)
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            newIntent.setComponent(ComponentName.unflattenFromString("$packageName/.MainActivity"))

            if (packageName.contains("spotify")) {
                newIntent.setComponent(ComponentName.unflattenFromString("$packageName/.SpotifyMainActivity"))
				newIntent.setAction("com.spotify.mobile.android.ui.action.player.SHOW")                	
            }
            else if (packageName.contains("youtube")) {
            	newIntent.setComponent(ComponentName.unflattenFromString("com.google.android.youtube/.api.StandalonePlayerActivity"))
            }
            else if (packageName.contains("com.piyush.music")) {
            	newIntent.setComponent(ComponentName.unflattenFromString("com.piyush.music/.activities.main.MainActivity"))
            }

            context.applicationContext.startActivity(newIntent)
        }
    }
}

class GrabSongLyricsOrLike : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val songTitle = parameters[MusicWidgetReceiver.songTitleKey]
        val isYoutube = parameters[MusicWidgetReceiver.isYoutubeKey]
        val artist = parameters[MusicWidgetReceiver.artistKey]

        if (songTitle == null || songTitle == "this is definitely most like absolutely not a song title lololol") {
            return
        }

        if (isYoutube == true) {
            NotificationListenerCustomService.likeYoutubeVideo()
            val intent = Intent(context, MusicWidgetReceiver::class.java).apply {
                action = MusicWidgetRefreshCallback.UPDATE_ACTION
            }
            context.sendBroadcast(intent)
        }
        else {
            val intent = Intent(Intent.ACTION_WEB_SEARCH)

            val query = if (artist != null && artist != "this is definitely most like absolutely not an artist lololol") {
                "$songTitle by $artist lyrics"
            }
            else {
                "$songTitle lyrics"
            }

            intent.putExtra(SearchManager.QUERY, query)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

            println("STARTED LYRICS")
            context.startActivity(intent)
        }
    }
}
