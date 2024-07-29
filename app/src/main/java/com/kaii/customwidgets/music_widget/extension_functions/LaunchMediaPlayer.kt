package com.kaii.customwidgets.music_widget.extension_functions

import android.content.Context
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.kaii.customwidgets.notification_listener_service.NotificationListenerCustomService

class LaunchMediaPlayer : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val packageName = NotificationListenerCustomService.getMediaPlayer()

        if (packageName != null) {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)	

            if (packageName.contains("spotify")) {
                // launchIntent.setComponent(ComponentName.unflattenFromString("$packageName/.SpotifyMainActivity"))
                launchIntent?.setAction("com.spotify.mobile.android.ui.action.player.SHOW")    
            }
             
            context.applicationContext.startActivity(launchIntent)

            // hah vibrator
            val vibrator = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager

            vibrator.vibrate(
                CombinedVibration.createParallel(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            )
        }
    }
}
