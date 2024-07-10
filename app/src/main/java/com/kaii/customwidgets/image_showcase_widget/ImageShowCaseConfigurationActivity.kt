package com.kaii.customwidgets.image_showcase_widget

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.star
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.toPath
import com.kaii.customwidgets.image_showcase_widget.styles.WidgetStyles
import com.kaii.customwidgets.ui.theme.*

class ImageShowCaseConfigurationActivity : ComponentActivity() {
    private lateinit var mediaPicker: ActivityResultLauncher<PickVisualMediaRequest>
    private var appWidgetId = 0
    private var chosenStyle = WidgetStyles.RoundedSquare
    private var pathData: String = ""

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
                
				pathData = "m69.7,128.4c1.58,-0.53 1.45,-0.44 9.59,-7.11 6.05,-4.96 7.52,-6.06 8.51,-6.36 0.66,-0.2 5.42,-0.78 10.59,-1.3 10.43,-1.04 10.84,-1.13 12.51,-2.83 1.71,-1.75 1.8,-2.11 2.82,-12.49 0.51,-5.15 1.07,-9.89 1.25,-10.52 0.2,-0.69 0.77,-1.7 1.44,-2.53 0.61,-0.76 3.37,-4.12 6.13,-7.46 5.6,-6.8 6.2,-7.77 6.2,-10.1 0,-2.3 -0.62,-3.33 -5.75,-9.55 -6.75,-8.16 -7.11,-8.62 -7.64,-9.68 -0.59,-1.17 -0.74,-2.22 -1.63,-11.66 -0.38,-4.04 -0.81,-7.92 -0.95,-8.61 -0.31,-1.56 -1.08,-2.92 -2.2,-3.91 -1.63,-1.43 -2.07,-1.52 -12.05,-2.52 -5.11,-0.51 -9.6,-0.99 -9.98,-1.07 -1.62,-0.33 -2.3,-0.84 -12.07,-8.9 -5.36,-4.42 -6.48,-5.07 -8.73,-5.07 -2.36,0 -3.27,0.57 -10.8,6.77 -7.17,5.9 -8.36,6.77 -9.65,7.11 -0.44,0.12 -5.04,0.63 -10.21,1.15 -10.33,1.03 -10.58,1.09 -12.39,2.75 -1.8,1.66 -1.91,2.12 -2.93,12.49 -0.5,5.08 -0.97,9.54 -1.05,9.92 -0.31,1.47 -1.4,2.99 -6.99,9.76 -6.43,7.77 -6.97,8.64 -6.97,11.02 0,2.33 0.6,3.31 6.2,10.1 2.76,3.35 5.51,6.71 6.13,7.46 0.67,0.83 1.25,1.83 1.44,2.53 0.18,0.63 0.74,5.36 1.24,10.5 0.75,7.69 1.01,9.55 1.41,10.5 0.56,1.3 2.27,3.03 3.52,3.55 0.5,0.21 4.43,0.7 10.19,1.28 5.16,0.51 9.68,1 10.06,1.08 1.4,0.29 2.64,1.17 9.41,6.73 3.8,3.12 7.25,5.9 7.66,6.19 1.63,1.13 3.81,1.44 5.71,0.8z"

                returnIntent.putExtra(
                    ImageShowCaseWidgetReceiver.EXTRA_IMAGE_SHOWCASE_PATH_DATA,
                    pathData
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
                            .background(MaterialTheme.colorScheme.tertiaryContainer),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // build a scrollable list of possible shapes for the widget
                        val list = listOf("Squared", "Circle", "Scallop", "Polygon", "Spikes", "Clover")
                        LazyColumn (
							modifier = Modifier
								.fillMaxSize(1f)
                           		.padding(16.dp),	
						) {
		                    itemsIndexed(list) { index, item ->
								BuildPreviewWidget(index, item)
	                    	}							
	                    }
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

    @Composable
    private fun ScaffoldBottomBar() {
        Column (
            modifier = Modifier
                .fillMaxWidth(1f)
                .height(96.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp, 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
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
                },
                modifier = Modifier
                	.fillMaxSize(1f)
                	.wrapContentHeight(Alignment.CenterVertically),
            ) {
                Text(
                    text = "Choose Background",
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

    @Composable
    private fun BuildPreviewWidget(index: Int, item: String) {

        lateinit var clip: Modifier
        lateinit var inClip: Modifier
        val backgroundColor = MaterialTheme.colorScheme.background
        val primaryColor = MaterialTheme.colorScheme.primary

        when (index) {
            0 -> { // rounded square
                clip = Modifier
                    .drawWithCache {
                        val roundedSquare = RoundedPolygon(
                            numVertices = 4,
                            radius = size.minDimension * 0.65f, // size fuckery should be fixed by clip support?
                            centerX = size.width / 2,
                            centerY = size.height / 2,
                            rounding = CornerRounding(
                            	radius = 36 * 2.5f
                            )
                        )

                        val roundedSquarePath = roundedSquare.toPath().asComposePath()

                        onDrawBehind {
                        	withTransform(
       							{
       								rotate(45f, center)
       							}
       						) {
	                            drawPath(roundedSquarePath, color = backgroundColor)		
       						}
                        }
                    }.fillMaxSize()
                inClip = Modifier
                    .drawWithCache {
                        val roundedSquare = RoundedPolygon(
                            numVertices = 4,
                            radius = size.minDimension * 0.65f, // size fuckery should be fixed by clip support?
                            centerX = size.width / 2,
                            centerY = size.height / 2,
                            rounding = CornerRounding(
                                radius = 28 * 2.5f
                            )
                        )

                        val roundedSquarePath = roundedSquare.toPath().asComposePath()

                        onDrawBehind {
                            withTransform(
                                {
                                    rotate(45f, center)
                                }
                            ) {
                                drawPath(roundedSquarePath, color = primaryColor)
                            }
                        }
                    }.fillMaxSize()
            }
            1 -> { // circle
                clip = Modifier
                    .drawWithCache {
                        val circle = RoundedPolygon.circle(
                            numVertices = 12,
                            radius = size.minDimension * 0.5f, // size fuckery should be fixed by clip support?
                            centerX = size.width / 2,
                            centerY = size.height / 2,
                        )

                        val circlePath = circle.toPath().asComposePath()

                        onDrawBehind {
                            drawPath(circlePath, color = backgroundColor)
                        }
                    }.fillMaxSize()
                inClip = Modifier
                    .drawWithCache {
                        val circle = RoundedPolygon.circle(
                            numVertices = 3,
                            radius = size.minDimension * 0.5f, // size fuckery should be fixed by clip support?
                            centerX = size.width / 2,
                            centerY = size.height / 2,
                        )

                        val circlePath = circle.toPath().asComposePath()

                        onDrawBehind {
                            drawPath(circlePath, color = primaryColor)
                        }
                    }.fillMaxSize()
            }
            2 -> { // scallop
                clip = Modifier
                    .drawWithCache {
                        val roundedStar = RoundedPolygon.star(
                            numVerticesPerRadius = 12,
                            radius = size.minDimension * 0.55f, // size fuckery should be fixed by clip support?
                            innerRadius = size.minDimension * 0.45f,
                            centerX = size.width / 2,
                            centerY = size.height / 2,
                            rounding = CornerRounding(
                                radius = 48 * 2.5f,
                            ),
                            innerRounding = CornerRounding(
                                radius = 48 * 2.5f,
                            )
                        )

                        val roundedSquarePath = roundedStar.toPath().asComposePath()

                        onDrawBehind {
                            drawPath(roundedSquarePath, color = backgroundColor)
                        }
                    }.fillMaxSize()
                inClip = Modifier
                    .drawWithCache {
                        val roundedStar = RoundedPolygon.star(
                            numVerticesPerRadius = 12,
                            radius = size.minDimension * 0.55f, // size fuckery should be fixed by clip support?
                            innerRadius = size.minDimension * 0.45f,
                            centerX = size.width / 2,
                            centerY = size.height / 2,
                            rounding = CornerRounding(
                                radius = 40 * 2.5f,
                            ),
                            innerRounding = CornerRounding(
                                radius = 40 * 2.5f,
                            )
                        )

                        val roundedSquarePath = roundedStar.toPath().asComposePath()

                        onDrawBehind {
                            drawPath(roundedSquarePath, color = primaryColor)
                        }
                    }.fillMaxSize()
            }
            3 -> { // polygon
            	clip = Modifier
                    .drawWithCache {
                        val roundedPolygon = RoundedPolygon(
                            numVertices = 5,
                            radius = size.minDimension * 0.55f, // size fuckery should be fixed by clip support?
                            centerX = size.width / 2,
                            centerY = size.height / 2,
                            rounding = CornerRounding(
                                radius = 48 * 2.5f,
                            ),
                        )

                        val roundedPolygonPath = roundedPolygon.toPath().asComposePath()

                        onDrawBehind {
                        	withTransform(
                                {
                                    rotate(55f, center)
                                }
                            ) {
                                drawPath(roundedPolygonPath, color = backgroundColor)
                            }
                        }
                    }.fillMaxSize()
                inClip = Modifier
                    .drawWithCache {
                        val roundedPolygon = RoundedPolygon(
                            numVertices = 5,
                            radius = size.minDimension * 0.55f, // size fuckery should be fixed by clip support?
                            centerX = size.width / 2,
                            centerY = size.height / 2,
                            rounding = CornerRounding(
                                radius = 40 * 2.5f,
                            ),
                        )

                        val roundedPolygonPath = roundedPolygon.toPath().asComposePath()

                        onDrawBehind {
                            withTransform(
                                {
                                    rotate(55f, center)
                                }
                            ) {
                                drawPath(roundedPolygonPath, color = primaryColor)
                            }
                        }
                    }.fillMaxSize()
            }
            4 -> { // spikes
            	clip = Modifier
                    .drawWithCache {
                        val roundedStar = RoundedPolygon.star(
                            numVerticesPerRadius = 8,
                            radius = size.minDimension * 0.52f, // size fuckery should be fixed by clip support?
                            innerRadius = size.minDimension * 0.42f,
                            centerX = size.width / 2,
                            centerY = size.height / 2,
                            rounding = CornerRounding(
                                radius = 20 * 2.5f,
                            ),
                            innerRounding = CornerRounding(
                                radius = 20 * 2.5f,
                            )
                        )

                        val roundedStarPath = roundedStar.toPath().asComposePath()
                        pathData = roundedStarPath.asAndroidPath().toString()

                        onDrawBehind {
                            drawPath(roundedStarPath, color = backgroundColor)
                        }
                    }.fillMaxSize()
                inClip = Modifier
                    .drawWithCache {
                        val roundedStar = RoundedPolygon.star(
                            numVerticesPerRadius = 8,
                            radius = size.minDimension * 0.52f, // size fuckery should be fixed by clip support?
                            innerRadius = size.minDimension * 0.42f,
                            centerX = size.width / 2,
                            centerY = size.height / 2,
                            rounding = CornerRounding(
                                radius = 12 * 2.5f,
                            ),
                            innerRounding = CornerRounding(
                                radius = 12 * 2.5f,
                            )
                        )

                        val roundedStarPath = roundedStar.toPath().asComposePath()

                        onDrawBehind {
                            drawPath(roundedStarPath, color = primaryColor)
                        }
                    }.fillMaxSize()
            }
            else -> { // clover
                clip = Modifier
                    .drawWithCache {
                        val roundedClover = RoundedPolygon.star(
                            numVerticesPerRadius = 4,
                            radius = size.minDimension * 0.75f, // size fuckery should be fixed by clip support?
							innerRadius = size.minDimension * 0.4f,
                            centerX = size.width / 2,
                            centerY = size.height / 2,
                            rounding = CornerRounding(
                                radius = 56 * 2.5f,
                            ),
                            innerRounding = CornerRounding(
	                            radius = 56 * 2.5f,
	                        ),
                        )

                        val roundedCloverPath = roundedClover.toPath().asComposePath()

                        onDrawBehind {
                        	withTransform(
                                {
                                    rotate(45f, center)
                                }
                            ) {
                                drawPath(roundedCloverPath, color = backgroundColor)
                            }
                        }
                    }.fillMaxSize()
                inClip = Modifier
                    .drawWithCache {
                        val roundedClover = RoundedPolygon.star(
                            numVerticesPerRadius = 4,
                            radius = size.minDimension * 0.75f, // size fuckery should be fixed by clip support?
							innerRadius = size.minDimension * 0.4f,
                            centerX = size.width / 2,
                            centerY = size.height / 2,
                            rounding = CornerRounding(
                                radius = 50f * 2.5f,
                            ),
                            innerRounding = CornerRounding(
                            	radius = 50f * 2.5f,
                            )
                        )

                        val roundedCloverPath = roundedClover.toPath().asComposePath()

                        onDrawBehind {
                        	withTransform(
                                {
                                    rotate(45f, center)
                                }
                            ) {
                                drawPath(roundedCloverPath, color = primaryColor)
                            }
                        }
                    }.fillMaxSize()
            }
        }
		
    	Column(
            modifier = Modifier
                .fillMaxSize(1f)
                .padding(4.dp)
                .background(Color.Transparent),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        	Column(
				modifier = Modifier
				.size(200.dp)
				.padding(4.dp)
				.background(Color.Transparent),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Row(
					modifier = Modifier
						.then(clip)
					    .fillMaxSize(1f) //.size(size.width * 1.25f)
					    .padding(8.dp)
					    .background(Color.Transparent),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.Center,
				) {
					Row(
						modifier = Modifier
                            .then(inClip)
						    .fillMaxSize(1f) //.size(size.width * 1.25f)
						    .padding(8.dp)
						    .background(Color.Transparent),
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.Center,
					) {

					}
				}
			}

			Text(text = item,
				maxLines = 1,
				textAlign = TextAlign.Center, // add font weight 
				modifier = Modifier
				.height(24.dp),
            )	
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
}



