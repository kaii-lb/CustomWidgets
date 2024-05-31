package com.kaii.customwidgets.musicwidget

import android.graphics.Bitmap
import android.media.session.MediaSession.QueueItem

data class MusicWidgetUIState (
    val artist: String?,
    val album: String?,
    val songTitle: String?,
    val length: Long,
    val position: Long,
    val albumArt: Bitmap,
    val queue: List<QueueItem>,
    val volume: Int,
    val maxVolume: Int,
    val likedYoutubeVideo: Boolean
) {
}