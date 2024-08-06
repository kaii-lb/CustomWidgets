package com.kaii.customwidgets.qstiles

import android.content.Context
import android.graphics.drawable.Icon
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraManager.TorchCallback
import android.os.Handler
import android.os.Looper
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class QSTileService : TileService() {
    companion object {
        var currentMode = false
        var frontOn = false
    }

    override fun onStartListening() {
        super.onStartListening()

        val context = super.getApplicationContext()
        val cameraManager = context.getSystemService(CAMERA_SERVICE) as CameraManager
        val handler = Handler(Looper.getMainLooper())

        class Callback : TorchCallback() {
            override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
                super.onTorchModeChanged(cameraId, enabled)

                println("CURRENT $currentMode and FRONT $frontOn")

				if (currentMode && frontOn) {
					qsTile.state =  Tile.STATE_ACTIVE
                    qsTile.subtitle = "360 On"
                    qsTile.icon = Icon.createWithResource(context, com.kaii.customwidgets.R.drawable.flash_all)
				}
                else if (currentMode && !frontOn) {
                    qsTile.state =  Tile.STATE_ACTIVE
                    qsTile.subtitle = "On"
                    qsTile.icon = Icon.createWithResource(context, com.kaii.customwidgets.R.drawable.flash_on)
                }
                else if (frontOn) {
                    qsTile.state =  Tile.STATE_ACTIVE
                    qsTile.subtitle = "Front On"
                    qsTile.icon = Icon.createWithResource(context, com.kaii.customwidgets.R.drawable.flash_front)
                }
                else if (!currentMode && !frontOn) {
                    qsTile.state = Tile.STATE_INACTIVE
                    qsTile.subtitle = ""
                    qsTile.icon = Icon.createWithResource(context, com.kaii.customwidgets.R.drawable.flash)
                }

                qsTile.updateTile()
            }
        }

        cameraManager.registerTorchCallback(Callback(), handler)
    }

    override fun onClick() {
        super.onClick()

        val context = super.getApplicationContext()
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraID = cameraManager.cameraIdList[0]


        val neededMode = if (qsTile.state == Tile.STATE_INACTIVE) {
        	if (frontOn) false else true
        }
        else {
        	if (frontOn && !currentMode) true else false
        }
        currentMode = neededMode
        println("TORCH ON $neededMode")
        cameraManager.setTorchMode(cameraID, neededMode)
    }

    override fun onTileAdded() {
        super.onTileAdded()

        qsTile.contentDescription = com.kaii.customwidgets.R.string.flashlight_tile_content_description.toString()
    }

    override fun onTileRemoved() {
        super.onTileRemoved()

        val context = super.getApplicationContext()
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraIDFront = cameraManager.cameraIdList[1]
        val cameraIDBack = cameraManager.cameraIdList[0]

        cameraManager.setTorchMode(cameraIDFront, false)
        cameraManager.setTorchMode(cameraIDBack, false)
    }
}

