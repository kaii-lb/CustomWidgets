package com.kaii.customwidgets

import android.content.Context
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraManager.TorchCallback
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class QSTileService : TileService() {
    private var torchModeON = false
    override fun onStartListening() {
        super.onStartListening()

        val context = super.getApplicationContext()
        val cameraManager = context.getSystemService(CAMERA_SERVICE) as CameraManager
        val handler = Handler(Looper.getMainLooper())

        class Callback : TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                super.onTorchModeChanged(cameraId, enabled)

                if (cameraId == "1") {
                    torchModeON = enabled
                }

                if (torchModeON) {
                    qsTile.state =  Tile.STATE_ACTIVE
                    qsTile.subtitle = "On"
                }
                else {
                    qsTile.state = Tile.STATE_INACTIVE
                    qsTile.subtitle = "Off"
                }

                qsTile.updateTile()
            }
        }

        cameraManager.registerTorchCallback(Callback(), handler)
    }

    override fun onStopListening() {
        super.onStopListening()
    }

    override fun onClick() {
        super.onClick()

        val context = super.getApplicationContext()
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraID = cameraManager.cameraIdList[1]

        cameraManager.setTorchMode(cameraID, !torchModeON)
    }

    override fun onTileRemoved() {
        super.onTileRemoved()

        val context = super.getApplicationContext()
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraID = cameraManager.cameraIdList[1]

        cameraManager.setTorchMode(cameraID, false)
    }
}

