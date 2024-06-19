package com.kaii.customwidgets.music_widget.extension_functions

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.kaii.customwidgets.music_widget.MusicWidgetReceiver
import com.kaii.customwidgets.music_widget.MusicWidgetRefreshCallback
import com.kaii.customwidgets.music_widget.NotificationListenerCustomService

class GetLyricsForSong : ActionCallback {
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

        // hah vibrator
        val vibrator = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

        vibrator.vibrate(CombinedVibration.createParallel(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK)))
    }
}
