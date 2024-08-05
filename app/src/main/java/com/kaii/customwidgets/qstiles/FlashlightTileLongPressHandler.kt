package com.kaii.customwidgets.qstiles

import android.app.Activity
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle

class FlashlightTileLongPressHandler : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toggleFrontFlash()
        finish()
    }

    private fun toggleFrontFlash() {
        val context = applicationContext
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraID = cameraManager.cameraIdList[1]

		val neededMode = !QSTileService.frontOn

		QSTileService.frontOn = neededMode
        cameraManager.setTorchMode(cameraID, neededMode)
    }
}
