package com.kaii.customwidgets.music_widget.extension_functions

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.kaii.customwidgets.music_widget.NotificationListenerCustomService

class LaunchMediaPlayer : ActionCallback {
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