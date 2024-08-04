package com.kaii.customwidgets.text_clock_widget

import android.content.Context
import android.content.Intent
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import com.kaii.customwidgets.text_clock_widget.TextClockWidgetReceiver

class ForceUpdateTextClockWidget : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
		val updateIntent = Intent(context.applicationContext, TextClockWidgetReceiver::class.java).apply {
            action = TextClockWidgetReceiver.FORCE_UPDATE_TEXT_CLOCK_ACTION
        }        
        context.applicationContext.sendBroadcast(updateIntent);
    }
}
