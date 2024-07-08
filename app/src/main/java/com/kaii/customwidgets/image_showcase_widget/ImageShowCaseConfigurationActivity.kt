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
import androidx.compose.ui.graphics.vector.PathParser
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
import com.kaii.customwidgets.ui.theme.*

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



