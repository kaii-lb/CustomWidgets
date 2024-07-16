package com.kaii.customwidgets.music_widget

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.media.MediaDescription
import android.media.MediaMetadata
import android.media.MediaRouter
import android.media.Rating
import android.media.ThumbnailUtils
import android.media.session.MediaController
import android.media.session.MediaSession.QueueItem
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationListenerCustomService : NotificationListenerService() {
    companion object {
        var mediaSessionManager: MediaSessionManager? = null
        var mediaController: MediaController? = null
        var metadata: MediaMetadata? = null
        var playbackState: Int = PlaybackState.STATE_STOPPED
        var volume: Int = 0
        var maxVolume: Int = 0
        private var likedYoutubeVideo: Boolean = false

        const val NOTIFICATION_LISTENER_CONFIG_CHANGED = "com.kaii.customwidgets.music_widget.NotificationListenerCustomService.NOTIFICATION_LISTENER_CONFIG_CHANGED"

        fun updateMetadata(): MusicWidgetUIState {
//            if (mediaController != null && mediaController!!.playbackState != null) {
//                //update widget play pause
//            }

//            println("UPDATED METADATA FROM INSIDE LISTENER")

            if (metadata == null) {
                val artist = "Not Available"
                val album = "Not Available"
                val songtitle = "Not Available"
                val length = 20.toLong()
                val position = 0.toLong()
                val albumArt = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_8888)
                val queue = List(10) {index ->
                    val description = MediaDescription.Builder().setTitle("Not Available").build()

                    QueueItem(description, index.toLong())
                }
                val volume = 0
                val maxVolume = 100
                val likedYoutubeVideo = false

                return MusicWidgetUIState(artist, album, songtitle, length, position, albumArt, queue, volume, maxVolume, likedYoutubeVideo)
            }

            val artist = if (metadata!!.getString(MediaMetadata.METADATA_KEY_ARTIST) == "") "Not Available" else metadata!!.getString(MediaMetadata.METADATA_KEY_ARTIST)
            val album = metadata!!.getString(MediaMetadata.METADATA_KEY_ALBUM)
            val songTitle = if (metadata!!.getString(MediaMetadata.METADATA_KEY_TITLE) == "") "Not Available" else metadata!!.getString(MediaMetadata.METADATA_KEY_TITLE)
            val length = metadata!!.getLong(MediaMetadata.METADATA_KEY_DURATION)
            val position = 0.0.toLong()

            val albumArtUnCropped = metadata!!.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART) ?: Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
            val albumArt = if (albumArtUnCropped.width != albumArtUnCropped.height) {
                ThumbnailUtils.extractThumbnail(albumArtUnCropped, 256, 352)
            } else {
                albumArtUnCropped
            }

            val queue = mediaController?.queue ?: List(10) { index ->
                val description = MediaDescription.Builder().setTitle("Not Available").build()

                QueueItem(description, index.toLong())
            }

            return MusicWidgetUIState(artist, album, songTitle, length, position, albumArt, queue, volume, maxVolume, likedYoutubeVideo)
        }

        fun skipForward() {
            mediaController?.transportControls?.skipToNext()
            println("SKIPPED FORWARDS")
        }
        fun skipBackward() {
            mediaController?.transportControls?.skipToPrevious()
            println("SKIPPED BACKWARDS")
        }
        fun playPause() {
            val state = mediaController?.playbackState?.state
            if (state == PlaybackState.STATE_PAUSED) {
                mediaController?.transportControls?.play()
            }
            else if (state == PlaybackState.STATE_PLAYING) {
                mediaController?.transportControls?.pause()
            }
        }

        fun playSongFromQueue(id: Long) {
            mediaController?.transportControls?.skipToQueueItem(id)
        }

        fun isYoutube() : Boolean {
            val packageName = mediaController?.packageName
//            val rating = mediaController?.metadata?.getRating(MediaMetadata.METADATA_KEY_RATING)?.isThumbUp

            return packageName?.lowercase()?.contains("youtube") == true
        }

        fun getMediaPlayer(): String? {
            return mediaController?.packageName
        }

        fun likeYoutubeVideo() {
            val rating = mediaController?.metadata?.getRating(MediaMetadata.METADATA_KEY_RATING)?.isThumbUp
            println("RATING $rating")

            if (rating == true) {
                mediaController?.transportControls?.setRating(Rating.newThumbRating(false))
                likedYoutubeVideo = false
            } else {
                mediaController?.transportControls?.setRating(Rating.newThumbRating(true))
                likedYoutubeVideo = true
            }
        }
    }

    private val componentName = ComponentName("com.kaii.customwidgets", "com.kaii.customwidgets.musicwidget.NotificationListenerCustomService")
    private var online = false

    private val sessionListener = MediaSessionManager.OnActiveSessionsChangedListener {controllers ->
        mediaController = controllers?.let { pickController(it) }

        if (mediaController != null) {
            mediaController!!.registerCallback(mediaControllerCallback)
            metadata = mediaController!!.metadata
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val intent = Intent(applicationContext, MusicWidgetReceiver::class.java).apply {
            action = NOTIFICATION_LISTENER_CONFIG_CHANGED
        }

        applicationContext.sendBroadcast(intent)
    }

    override fun onCreate() {
        super.onCreate()

        if (!Settings.Secure.getString(applicationContext.contentResolver, "enabled_notification_listeners").contains(applicationContext.packageName)) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        mediaSessionManager!!.addOnActiveSessionsChangedListener(sessionListener, componentName)

        val controllers = mediaSessionManager!!.getActiveSessions(componentName)
        mediaController = pickController(controllers)

        if (mediaController != null) {
            mediaController!!.registerCallback(mediaControllerCallback)
            metadata = mediaController!!.metadata
        }

        val mediaRouter = applicationContext.getSystemService(MEDIA_ROUTER_SERVICE) as MediaRouter
        mediaRouter.addCallback(MediaRouter.ROUTE_TYPE_USER, mediaRouterCallback, MediaRouter.CALLBACK_FLAG_UNFILTERED_EVENTS)

        online = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (mediaController == null) {
            val controllers = mediaSessionManager!!.getActiveSessions(componentName)
            mediaController = pickController(controllers)

            if (mediaController != null) {
                mediaController!!.registerCallback(mediaControllerCallback)
                metadata = mediaController!!.metadata
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {

    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaController = null
        online = false

        val intent = Intent(applicationContext, MusicWidgetReceiver::class.java).apply {
            action = MusicWidgetRefreshCallback.UPDATE_ACTION
        }
        applicationContext.sendBroadcast(intent)
        
        mediaSessionManager?.removeOnActiveSessionsChangedListener(sessionListener)
    }

    private fun pickController(controllers: List<MediaController>): MediaController? {
        for (controller in controllers) {
            if (controller.playbackState?.state == PlaybackState.STATE_PLAYING || controller.playbackState?.state == PlaybackState.STATE_BUFFERING) {
                return controller
            }
        }

        if (controllers.isNotEmpty()) return controllers[0]
        return null
    }

    private var mediaControllerCallback: MediaController.Callback = object : MediaController.Callback() {
        override fun onSessionDestroyed() {
            super.onSessionDestroyed()
            mediaController = null
            metadata = null
            likedYoutubeVideo = false

            println("LOG: SESSION DESTROYED")

            val intent = Intent(applicationContext, MusicWidgetReceiver::class.java).apply {
                action = MusicWidgetRefreshCallback.UPDATE_ACTION
            }

            applicationContext.sendBroadcast(intent)
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            super.onPlaybackStateChanged(state)

            playbackState = state?.state ?: PlaybackState.STATE_STOPPED
            likedYoutubeVideo = false

            val intent = Intent(applicationContext, MusicWidgetReceiver::class.java).apply {
                action = MusicWidgetRefreshCallback.STATE_ACTION
            }
            println("PLAYBACK STATE CHANGED")

            applicationContext.sendBroadcast(intent)
        }

        override fun onMetadataChanged(newMetadata: MediaMetadata?) {
            super.onMetadataChanged(newMetadata)

            metadata = newMetadata
            println("METADATA CHANGED")
            likedYoutubeVideo = false

            val intent = Intent(applicationContext, MusicWidgetReceiver::class.java).apply {
                action = MusicWidgetRefreshCallback.UPDATE_ACTION
            }
            val playbackIntent = Intent(applicationContext, MusicWidgetReceiver::class.java).apply {
                action = MusicWidgetRefreshCallback.STATE_ACTION
            }

            applicationContext.sendBroadcast(intent)

            Handler(Looper.getMainLooper()).postDelayed({
            	sendBroadcast(playbackIntent)
            }, 150)
            
            Handler(Looper.getMainLooper()).postDelayed({
                sendBroadcast(playbackIntent);
                println("UPDATED WITH DELAY")
            }, 2000)
        }

        override fun onQueueChanged(queue: MutableList<QueueItem>?) {
            super.onQueueChanged(queue)

            val intent = Intent(applicationContext, MusicWidgetReceiver::class.java).apply {
                action = MusicWidgetRefreshCallback.UPDATE_ACTION
            }
            applicationContext.sendBroadcast(intent)
            likedYoutubeVideo = false
        }
    }

    private var mediaRouterCallback: MediaRouter.Callback = object : MediaRouter.Callback() {
        override fun onRouteSelected(
            router: MediaRouter?,
            type: Int,
            info: MediaRouter.RouteInfo?
        ) {
            volume = info?.volume ?: 0
            maxVolume = info?.volumeMax ?: 100

            val intent = Intent(applicationContext, MusicWidgetReceiver::class.java).apply {
                action = MusicWidgetRefreshCallback.VOLUME_ACTION
            }
            applicationContext.sendBroadcast(intent)
        }

        override fun onRouteUnselected(
            router: MediaRouter?,
            type: Int,
            info: MediaRouter.RouteInfo?
        ) {
            volume = info?.volume ?: 0
            maxVolume = info?.volumeMax ?: 100

            val intent = Intent(applicationContext, MusicWidgetReceiver::class.java).apply {
                action = MusicWidgetRefreshCallback.VOLUME_ACTION
            }
            applicationContext.sendBroadcast(intent)
        }

        override fun onRouteAdded(router: MediaRouter?, info: MediaRouter.RouteInfo?) {}

        override fun onRouteRemoved(router: MediaRouter?, info: MediaRouter.RouteInfo?) {}

        override fun onRouteChanged(router: MediaRouter?, info: MediaRouter.RouteInfo?) {}

        override fun onRouteGrouped(
            router: MediaRouter?,
            info: MediaRouter.RouteInfo?,
            group: MediaRouter.RouteGroup?,
            index: Int
        ) {}

        override fun onRouteUngrouped(
            router: MediaRouter?,
            info: MediaRouter.RouteInfo?,
            group: MediaRouter.RouteGroup?
        ) {}

        override fun onRouteVolumeChanged(router: MediaRouter?, info: MediaRouter.RouteInfo?) {
            volume = info?.volume ?: 0
            maxVolume = info?.volumeMax ?: 100

            val intent = Intent(applicationContext, MusicWidgetReceiver::class.java).apply {
                action = MusicWidgetRefreshCallback.VOLUME_ACTION
            }
            applicationContext.sendBroadcast(intent)
        }
    }
}

//class Callback : MediaController.Callback() {
//    override fun onSessionDestroyed() {
//        super.onSessionDestroyed()
//        mediaController = null
//        metadata = null
//    }
//
//    override fun onSessionEvent(event: String, extras: Bundle?) {
//        super.onSessionEvent(event, extras)
//    }
//
//    override fun onMetadataChanged(newMetadata: MediaMetadata?) {
//        super.onMetadataChanged(newMetadata)
//
//        metadata = newMetadata
//        println("METADATA CHANGED")
//    }
//
//    override fun onQueueChanged(queue: MutableList<MediaSession.QueueItem>?) {
//        super.onQueueChanged(queue)
//    }
//}
