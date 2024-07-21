package com.kaii.customwidgets.image_showcase_widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.VibratorManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import com.kaii.customwidgets.image_showcase_widget.selection_list.SelectionModel
import com.kaii.customwidgets.image_showcase_widget.selection_list.SingleSelectionList
import com.kaii.customwidgets.image_showcase_widget.styles.WidgetStyles
import com.kaii.customwidgets.ui.theme.*
import java.io.File

class ImageShowCaseConfigurationActivity : ComponentActivity() {
    private lateinit var mediaPicker: ActivityResultLauncher<PickVisualMediaRequest>
    private var appWidgetId = 0

    companion object {
    	var chosenStyle = WidgetStyles.RoundedSquare
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setResult(RESULT_CANCELED)

        // run activity so its result is returned to widget
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
                    ImageShowCaseWidgetReceiver.EXTRA_IMAGE_SHOWCASE_CHOSEN_STYLE,
                    chosenStyle
                )

                returnIntent.putExtra(
                    ImageShowCaseWidgetReceiver.EXTRA_IMAGE_SHOWCASE_BACKGROUND_URI,
                    uri
                )

                // set exit result of activity and send intent to widget
                setResult(RESULT_OK, returnIntent)
                sendBroadcast(returnIntent)

                finish()
            } else {
                println("FAILED CHOOSING PHOTO")

				// exit if no image was picked/back button pressed
                //finish()
            }
        }

        setContent {
            WidgetsTheme {
                Content()
            }
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
            Scaffold (
                topBar = {
                    ScaffoldTopBar()
                },
                bottomBar = {
                    ScaffoldBottomBar()
                }
            ) { padding ->
                Column (
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column (
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize(1f)
                            .clip(RoundedCornerShape(32.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainer),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val model = SelectionModel()
                        SingleSelectionList(model.options, model::selectionOptionSelected)

                        model.selectionOptionSelected(model.options[0])
                    }
                }
            }
        }
    }

    @Composable
    private fun ScaffoldTopBar() {
        Column (
            modifier = Modifier
                .fillMaxWidth(1f)
                .height(64.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text (
                text = "Pick Widget Style",
                textAlign = TextAlign.Center,
                fontSize = TextUnit(32f, TextUnitType.Sp)
            )
        }
    }

	@OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun ScaffoldBottomBar() {
        Row (
            modifier = Modifier
                .fillMaxWidth(1f)
                .height(96.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(12.dp, 4.dp, 12.dp, 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column (
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .height(96.dp)
                    .clip(RoundedCornerShape(1000.dp, 16.dp, 16.dp, 1000.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .combinedClickable(
                        onClick = {
                            chooseStyle()
                        },
                    )
                    .padding(22.dp, 12.dp, 24.dp, 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Style",
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .fillMaxSize(1f)
                        .wrapContentHeight(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                    fontSize = TextUnit(24f, TextUnitType.Sp),
                    maxLines = 1,
                )
            }
            
            Column (
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(96.dp)
                    .clip(RoundedCornerShape(16.dp, 1000.dp, 1000.dp, 16.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .combinedClickable(
                        onClick = {
                            configAppWidget()

                            val vibrator = applicationContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                            vibrator.vibrate(
                                CombinedVibration.createParallel(
                                    VibrationEffect.createOneShot(60, 120)
                                )
                            )
                        },
                    )
                    .padding(8.dp, 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Background",
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .fillMaxSize(1f)
                        .wrapContentHeight(Alignment.CenterVertically),
                    textAlign = TextAlign.Center,
                    fontSize = TextUnit(24f, TextUnitType.Sp),
                    maxLines = 1,
                )
            }
        }
    }

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

    fun chooseStyle() {
    	val intent = intent
        val extras = intent.extras

        if (extras == null) finish()

        appWidgetId = extras!!.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        )

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) finish()
        // val returnIntent = Intent(
        // 	ImageShowCaseWidgetReceiver.IMAGE_SHOWCASE_WIDGET_SET_BACKGROUND_ACTION
        // )

        val returnIntent = Intent(
            applicationContext,
            ImageShowCaseWidgetReceiver::class.java
        ).apply {
            action =
                ImageShowCaseWidgetReceiver.IMAGE_SHOWCASE_WIDGET_SET_BACKGROUND_ACTION
        }

        returnIntent.putExtra(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            appWidgetId
        )

        returnIntent.putExtra(
            ImageShowCaseWidgetReceiver.EXTRA_IMAGE_SHOWCASE_CHOSEN_STYLE,
            chosenStyle
        )

        returnIntent.putExtra(
            ImageShowCaseWidgetReceiver.EXTRA_IMAGE_SHOWCASE_BACKGROUND_URI,
            Uri.parse("null")
        )

        val filePath = applicationContext.getExternalFilesDir("backgrounds")?.path + "/image_$appWidgetId.png"
        val vibrator = applicationContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        
        if (!File(filePath).exists()) {
            Toast.makeText(applicationContext, "Select background and style first", Toast.LENGTH_LONG).show()

			val vibeEffect = VibrationEffect.createOneShot(500, 188)

            vibrator.vibrate(
                CombinedVibration.createParallel(
                    vibeEffect
                )
            )
        } else {
	        // set exit result of activity and send intent to widget
	        setResult(RESULT_OK, returnIntent)
	        sendBroadcast(returnIntent)

	        vibrator.vibrate(
	            CombinedVibration.createParallel(
	                VibrationEffect.createOneShot(60, 120)
	            )
	        )
         
        	finish()  	
        }
    }
}



