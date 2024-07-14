package com.kaii.customwidgets.image_showcase_widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Matrix
import android.graphics.RectF
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.core.graphics.PathParser
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.ImageProvider
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.unit.ColorProvider
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.glance.layout.Alignment
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.kaii.customwidgets.image_showcase_widget.styles.StylePathData
import com.kaii.customwidgets.image_showcase_widget.styles.WidgetStyles
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

const val IMAGE_SHOWCASE_WIDGET_TAG = "IMAGE_SHOWCASE_WIDGET"

class ImageShowCaseWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition
    override val sizeMode = SizeMode.Exact

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val backgroundUri = prefs[ImageShowCaseWidgetReceiver.backgroundUriString] ?: ""
            val appWidgetId = prefs[ImageShowCaseWidgetReceiver.appWidgetIdInt] ?: 123456789

            val styleOrdinal = prefs[ImageShowCaseWidgetReceiver.chosenStyle] ?: 0
            val style = WidgetStyles.entries[styleOrdinal]
		
			// add something for 3 cell sizes, its fucks up
            GlanceTheme {
                Content(backgroundUri, appWidgetId, style)
            }
        }
    }

    @Composable
    private fun Content(backgroundUri: String, appWidgetId: Int, style: WidgetStyles) {
        //val size = LocalSize.current
        val uri = Uri.parse(backgroundUri)
        val context = LocalContext.current.applicationContext
        if(appWidgetId == 123456789) return

        // set files from persistent file dir
        val filePath = context.getExternalFilesDir("backgrounds")?.path + "/image_$appWidgetId.png"
        val folder = File(context.getExternalFilesDir("backgrounds")?.path ?: return)
		if (!folder.exists()) {
			folder.mkdir()
		}
		val file = File(filePath)

        // try opening URI, set to null if it fails so we can possibly load previously set image from storage
        val inputStream = try {
            context.contentResolver.openInputStream(uri)
        } catch (e: Exception) {
            Log.d(IMAGE_SHOWCASE_WIDGET_TAG, "permission denied for URI: $uri")
            null
        }

        // get the max memory allowed to be used by a single image for this device
        val displayMetrics = Resources.getSystem().displayMetrics
        val maxMemoryUsage = displayMetrics.widthPixels * displayMetrics.heightPixels * 4 * 1.5f // display resolution * 4 bytes per pixel * 1.5 is the memory limit

		lateinit var holderBitmap: Bitmap

        // try setting background, no IS and no file we exit
        // IS exists then load from it
        // otherwise try to load previously set image
        if (inputStream == null && !file.exists()) {
            ZeroState()
            return
        }
        else if (inputStream != null) {
            holderBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
        }
        else {
        	inputStream?.close()
            holderBitmap = BitmapFactory.decodeFile(filePath)
        }

        // try saving image to persistent storage, on fail show error message to user
        try {
            val fileOutputStream = FileOutputStream(file)
            holderBitmap.compress(Bitmap.CompressFormat.PNG, 25, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: Exception) {
            Log.e("ERROR SAVING FILE", e.message ?: "file save failed")

            Toast.makeText(context, "can't save background image", Toast.LENGTH_SHORT).show()
        }

        val options = BitmapFactory.Options()
        options.inSampleSize = 2 // play with this to get best resolution for screen size

		// it may work...it may not, idfk
        if (holderBitmap.byteCount >= maxMemoryUsage) {
            Log.d(IMAGE_SHOWCASE_WIDGET_TAG, "bitmap (${holderBitmap.byteCount} bytes) exceeds device memory limit ($maxMemoryUsage bytes), compressing...")
            options.inSampleSize = 4
        }
        val backgroundBitmap = BitmapFactory.decodeFile(filePath, options)

        if (backgroundBitmap == null) {
            ZeroState()
            Toast.makeText(context, "could not load widget background", Toast.LENGTH_SHORT).show()
            return 
        }

		Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding((-8).dp)
                .cornerRadius(24.dp)
                .background(ColorProvider(Color.Transparent)),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        ) {
        	// try to keep a difference of 8.dp between each layer
            when (style) {
                WidgetStyles.RoundedSquare -> {
	                Row(
	                    modifier = GlanceModifier
	                        .fillMaxSize() //.size(size.width * 1.25f)
	                        .padding(8.dp)
	                        .background(ColorProvider(Color.Transparent)),
	                    verticalAlignment = Alignment.Vertical.CenterVertically,
	                    horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

	                ) {
	                	val size = LocalSize.current
	                	val neededSize = if (size.height >= size.width) {
	                		size.width
	                	} else {
	                		size.height
	                	}
	                    Row(
	                        modifier = GlanceModifier
	                            .size(neededSize - 8.dp)
	                            .cornerRadius(24.dp)
	                            .padding(8.dp)
	                            .background(GlanceTheme.colors.widgetBackground),
	                        verticalAlignment = Alignment.Vertical.CenterVertically,
	                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

	                    ) {
	                        Image(
	                            provider = ImageProvider(backgroundBitmap),
	                            modifier = GlanceModifier
	                                .defaultWeight()
	                                .fillMaxSize() //.size(size.width * 1.25f)
	                                .cornerRadius(16.dp),
	                            contentScale = ContentScale.Crop,
	                            contentDescription = "image showing user-selected background"
	                        )
	                    }
                    }
                }
                WidgetStyles.Circle -> {
	                Row(
                        modifier = GlanceModifier
                            .fillMaxSize() //.size(size.width * 1.25f)
                            .padding(8.dp)
                            .background(ColorProvider(Color.Transparent)),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

                    ) {
                    	val size = LocalSize.current
                    	val neededSize = if (size.height >= size.width) {
                    		size.width
                    	} else {
                    		size.height
                    	}
	                    Row(
	                        modifier = GlanceModifier
	                            .size(neededSize - 4.dp)
	                            .cornerRadius(1000.dp)
	                            .padding(8.dp)
	                            .background(GlanceTheme.colors.widgetBackground),
	                        verticalAlignment = Alignment.Vertical.CenterVertically,
	                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

	                    ) {
	                        Image(
	                            provider = ImageProvider(backgroundBitmap),
	                            modifier = GlanceModifier
	                                .fillMaxSize() //.size(size.width * 1.25f)
	                                .cornerRadius(1000.dp),
	                            contentScale = ContentScale.Crop,
	                            contentDescription = "image showing user-selected background"
	                        )
	                    }
                    }
                }
                WidgetStyles.Scallop -> {
                    val backgroundImage = ImageProvider(com.kaii.customwidgets.R.drawable.image_showcase_scallop)

					Row(
                        modifier = GlanceModifier
                            .fillMaxSize() //.size(size.width * 1.25f)
                            .padding(4.dp)
                            .background(ColorProvider(Color.Transparent)),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

                    ) {
                    	val size = LocalSize.current
                    	val neededSize = if (size.height >= size.width) {
                    		size.width
                    	} else {
                    		size.height
                    	}

	                    Row(
	                        modifier = GlanceModifier
	                            .size(neededSize + 12.dp)
	                            .cornerRadius(24.dp)
	                            .padding(4.dp)
	                            .background(backgroundImage, ContentScale.Fit, ColorFilter.tint(GlanceTheme.colors.widgetBackground)),
	                        verticalAlignment = Alignment.Vertical.CenterVertically,
	                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

	                    ) {
	                        BuildWidgetStyle(stylePathData = StylePathData.ScallopData, bitmap = backgroundBitmap, neededSize + 4.dp)
	                    }
                    }
                }
                WidgetStyles.Polygon -> {
                    val backgroundImage = ImageProvider(com.kaii.customwidgets.R.drawable.image_showcase_polygon)
					Row(
                        modifier = GlanceModifier
                            .fillMaxSize() //.size(size.width * 1.25f)
                            .padding(4.dp)
                            .background(ColorProvider(Color.Transparent)),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

                    ) {
                    	val size = LocalSize.current
                    	val neededSize = if (size.height >= size.width) {
                    		size.width
                    	} else {
                    		size.height
                    	}

	                    Row(
	                        modifier = GlanceModifier
	                            .size(neededSize + 12.dp)
	                            .cornerRadius(24.dp)
	                            .padding(4.dp)
	                            .background(backgroundImage, ContentScale.Fit, ColorFilter.tint(GlanceTheme.colors.widgetBackground)),
	                        verticalAlignment = Alignment.Vertical.CenterVertically,
	                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

	                    ) {
	                        BuildWidgetStyle(stylePathData = StylePathData.PolygonData, bitmap = backgroundBitmap, neededSize + 8.dp)
	                    }
                   	}
                }
                WidgetStyles.Spikes -> {
                    val backgroundImage = ImageProvider(com.kaii.customwidgets.R.drawable.image_showcase_spikes)
					Row(
                        modifier = GlanceModifier
                            .fillMaxSize() //.size(size.width * 1.25f)
                            .padding(4.dp)
                            .background(ColorProvider(Color.Transparent)),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

                    ) {
                    	val size = LocalSize.current
                    	val neededSize = if (size.height >= size.width) {
                    		size.width
                    	} else {
                    		size.height
                    	}

	                    Row(
	                        modifier = GlanceModifier
	                            .size(neededSize + 16.dp)
	                            .cornerRadius(24.dp)
	                            .padding(4.dp)
	                            .background(backgroundImage, ContentScale.Fit, ColorFilter.tint(GlanceTheme.colors.widgetBackground)),
	                        verticalAlignment = Alignment.Vertical.CenterVertically,
	                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

	                    ) {
	                        BuildWidgetStyle(stylePathData = StylePathData.SpikesData, bitmap = backgroundBitmap, neededSize + 12.dp)
	                    }
                   	}
                }
                WidgetStyles.Clover -> {
                    val backgroundImage = ImageProvider(com.kaii.customwidgets.R.drawable.image_showcase_clover)

					Row(
                        modifier = GlanceModifier
                            .fillMaxSize() //.size(size.width * 1.25f)
                            .padding(4.dp)
                            .background(ColorProvider(Color.Transparent)),
                        verticalAlignment = Alignment.Vertical.CenterVertically,
                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

                    ) {
                    	val size = LocalSize.current
                    	val neededSize = if (size.height >= size.width) {
                    		size.width
                    	} else {
                    		size.height
                    	}

	                    Row(
	                        modifier = GlanceModifier
	                            .size(neededSize + 12.dp)
	                            .cornerRadius(32.dp)
	                            .padding(4.dp)
	                            .background(backgroundImage, ContentScale.Fit, ColorFilter.tint(GlanceTheme.colors.widgetBackground)),
	                        verticalAlignment = Alignment.Vertical.CenterVertically,
	                        horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

	                    ) {
	                        BuildWidgetStyle(stylePathData = StylePathData.CloverData, bitmap = backgroundBitmap, neededSize + 8.dp)
	                    }
                   	}
                }
            }
        }
        Log.d(IMAGE_SHOWCASE_WIDGET_TAG, "updated background successfully")
    }

    // "oh shit" state
    @Composable
    private fun ZeroState() {
        Column (
            modifier = GlanceModifier
                .appWidgetBackground()
                .fillMaxSize()
                .padding(16.dp)
                .background(GlanceTheme.colors.widgetBackground),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        ) {
            Text(
            	text = "Couldn't Load Image :(",
                style = TextStyle(color = GlanceTheme.colors.primary, fontSize = TextUnit(16.0f, TextUnitType.Sp))
           	)
        }
    }

    @Composable
    private fun BuildWidgetStyle(stylePathData: StylePathData, bitmap: Bitmap, neededSize: Dp) {
        val shapedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val shapedCanvas = Canvas(shapedBitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            color = Color.Black.toArgb()
        }
        val path = PathParser.createPathFromPathData(stylePathData.data)
        val scaleMatrix = Matrix()
        val rectF = RectF()
        path.computeBounds(rectF, true)
        scaleMatrix.setScale((shapedCanvas.width / rectF.width()) / 1.1f, (shapedCanvas.width / rectF.width()) / 1.1f, rectF.centerX(), rectF.centerY())
        path.transform(scaleMatrix)
        path.offset((shapedCanvas.width - rectF.width() - 12.5f) / 2f, (shapedCanvas.height - rectF.height() - 12.5f) / 2f)

        shapedCanvas.drawPath(path, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        shapedCanvas.drawBitmap(bitmap, 0f, 0f, paint)

        Image(
            provider = ImageProvider(shapedBitmap),
            modifier = GlanceModifier
                .size(neededSize - 15.dp),
            contentScale = ContentScale.Crop,
            contentDescription = "image showing user-selected background"
        )
    }
}

class ImageShowCaseWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ImageShowCaseWidget()

    companion object {
        val backgroundUriString = stringPreferencesKey("background_uri")
        val appWidgetIdInt = intPreferencesKey("app_widget_id")
        val chosenStyle = intPreferencesKey("chosen_style")

        const val IMAGE_SHOWCASE_WIDGET_SET_BACKGROUND_ACTION =
            "com.kaii.customwidgets.image_showcase_widget.IMAGE_SHOWCASE_WIDGET_SET_BACKGROUND_ACTION"
        const val EXTRA_IMAGE_SHOWCASE_BACKGROUND_URI =
            "com.kaii.customwidgets.image_showcase_widget.EXTRA_IMAGE_SHOWCASE_BACKGROUND_URI"
        const val EXTRA_IMAGE_SHOWCASE_CHOSEN_STYLE =
            "com.kaii.customwidgets.image_showcase_widget.EXTRA_IMAGE_SHOWCASE_CHOSEN_STYLE"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // update widget when the right intent is received
        // hacky workaround but too lazy to implement correct method and this works perfectly fine
        if (intent.action == IMAGE_SHOWCASE_WIDGET_SET_BACKGROUND_ACTION) {
            val appWidgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID)
            val chosenStyle = intent.extras?.getSerializable(EXTRA_IMAGE_SHOWCASE_CHOSEN_STYLE, WidgetStyles::class.java)
            val backgroundUri =
                intent.extras?.getParcelable(EXTRA_IMAGE_SHOWCASE_BACKGROUND_URI, Uri::class.java)

            setImageShowCaseWidgetBackground(context, appWidgetId, chosenStyle, backgroundUri)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // loop over all removed widgets and delete their associated files
        for (id in appWidgetIds) {
            val filePath = context.getExternalFilesDir("backgrounds")?.path + "/image_$id.png"
            val file = File(filePath)

            if (file.delete()) {
                Log.d(IMAGE_SHOWCASE_WIDGET_TAG, "background for $id was deleted upon removal of widget")
            }
            else {
                Log.d(IMAGE_SHOWCASE_WIDGET_TAG, "error occurred, background for $id was NOT deleted")
            }
        }

        super.onDeleted(context, appWidgetIds)
    }

    private fun setImageShowCaseWidgetBackground(
        context: Context,
        appWidgetId: Int?,
        nullableStyle: WidgetStyles?,
        backgroundUri: Uri?
    ) {
        MainScope().launch {
            val manager = GlanceAppWidgetManager(context)
            val uriString = backgroundUri.toString()
            val style = nullableStyle ?: WidgetStyles.RoundedSquare
            Log.d(IMAGE_SHOWCASE_WIDGET_TAG, "URI of picked image is: $uriString")

            val glanceID = manager.getGlanceIdBy(appWidgetId ?: return@launch)
            glanceID.let {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                    pref.toMutablePreferences().apply {
                        this[backgroundUriString] = uriString
                        this[appWidgetIdInt] = appWidgetId
                        this[chosenStyle] = style.ordinal
                    }
                }
                glanceAppWidget.update(context, it)
            }
        }
    }
}




