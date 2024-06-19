package com.kaii.customwidgets.image_showcase_widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class ImageShowCaseConfigurationActivity : ComponentActivity() {
    private lateinit var mediaPicker: ActivityResultLauncher<PickVisualMediaRequest>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(RESULT_CANCELED)

        mediaPicker = registerForActivityResult(PickVisualMedia()) { uri ->
            if (uri != null) {
                println("PICKED PHOTO: $uri")
                applicationContext.grantUriPermission(applicationContext.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)

                // val returnIntent = Intent(
               	// 	ImageShowCaseWidgetReceiver.IMAGE_SHOWCASE_WIDGET_SET_BACKGROUND_ACTION
                // )

                val returnIntent = Intent(applicationContext, ImageShowCaseWidgetReceiver::class.java).apply {
                	action = ImageShowCaseWidgetReceiver.IMAGE_SHOWCASE_WIDGET_SET_BACKGROUND_ACTION
            	}

                returnIntent.putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId
                )

                returnIntent.putExtra(
                    ImageShowCaseWidgetReceiver.EXTRA_IMAGE_SHOWCASE_BACKGROUND_URI,
                    uri
                )

                setResult(RESULT_OK, returnIntent)
                sendBroadcast(returnIntent)

                finish()
            } else {
                println("FAILED CHOOSING PHOTO")

                finish()
            }
        }

        setContent {
            Content()
            println("SET CONTENT")
        }
    }

    @Preview
    @Composable
    private fun Content() {
        Column(
            modifier = Modifier
                .fillMaxSize(1f)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = Color.DarkGray,
                    disabledContentColor = Color.Gray
                ),
                onClick = {
                    configAppWidget()
                }
            ) {
                Text(
                    text = "Choose Picture",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

    private var appWidgetId = 0
    private fun configAppWidget() {
        val intent = intent
        val extras = intent.extras

        if (extras == null) finish()

        appWidgetId = extras!!.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) finish()

        // TODO: configure widget + get AppWidgetManager instance

        mediaPicker.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
    }
}
