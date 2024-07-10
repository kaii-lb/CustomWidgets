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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import androidx.glance.action.clickable
import androidx.glance.action.actionStartActivity
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
import androidx.glance.layout.Alignment
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import com.kaii.customwidgets.image_showcase_widget.styles.StylePathData
import com.kaii.customwidgets.image_showcase_widget.styles.WidgetStyles
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

const val IMAGE_SHOWCASE_WIDGET_TAG = "IMAGE_SHOWCASE_WIDGET"

class ImageShowCaseWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    companion object {
        private val THREE_CELLS = DpSize(250.dp, 250.dp)
        private val TWO_CELLS = DpSize(110.dp, 110.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(
            TWO_CELLS, THREE_CELLS
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val prefs = currentState<Preferences>()
            val backgroundUri = prefs[ImageShowCaseWidgetReceiver.backgroundUriString] ?: ""
            val appWidgetId = prefs[ImageShowCaseWidgetReceiver.appWidgetIdInt] ?: 123456789

            val styleOrdinal = prefs[ImageShowCaseWidgetReceiver.chosenStyle] ?: 0
            val style = WidgetStyles.entries[styleOrdinal]

            val pathData = prefs[ImageShowCaseWidgetReceiver.pathData] ?: ""

			// add something for 3 cell sizes, its fucks up
            GlanceTheme {
                Content(backgroundUri, appWidgetId, style, pathData)
            }
        }
    }

    @Composable
    private fun Content(backgroundUri: String, appWidgetId: Int, style: WidgetStyles, pathData: String) {
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

		// this is failing miserably(?), change it
        if (holderBitmap.byteCount >= maxMemoryUsage) {
        	println("TOO MUCH MEMORY USAGE")
            Log.d(IMAGE_SHOWCASE_WIDGET_TAG, "bitmap exceeds device memory limit ($maxMemoryUsage bytes), compressing...")
            options.inSampleSize = 4
        }
        val backgroundBitmap = BitmapFactory.decodeFile(filePath, options)

        if (backgroundBitmap == null) {
            ZeroState()
            return // and show a toast
        }

        val backgroundImage = ImageProvider(com.kaii.customwidgets.R.drawable.image_showcase_spikes)

		Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(0.dp)
                .cornerRadius(20.dp)
                .background(ColorProvider(Color.Transparent)),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        ) {
            Row(
                modifier = GlanceModifier
                    .fillMaxSize() //.size(size.width * 1.25f)
                    // .cornerRadius(24.dp)
                    .padding(4.dp)
                    .clickable(actionStartActivity<ImageShowCaseConfigurationActivity>())
                    .background(backgroundImage, ContentScale.Fit, ColorFilter.tint(GlanceTheme.colors.widgetBackground)),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

            ) {
                when (style) {
                    WidgetStyles.RoundedSquare -> {
                        Image(
                            provider = ImageProvider(backgroundBitmap),
                            modifier = GlanceModifier
                                .fillMaxSize() //.size(size.width * 1.25f)
                                .cornerRadius(24.dp),
                            contentScale = ContentScale.Crop,
                            contentDescription = "image showing user-selected background"
                        )
                    }
                    WidgetStyles.Circle -> {
                        Image(
                            provider = ImageProvider(backgroundBitmap),
                            modifier = GlanceModifier
                                .fillMaxSize() //.size(size.width * 1.25f)
                                .cornerRadius(1000.dp),
                            contentScale = ContentScale.Crop,
                            contentDescription = "image showing user-selected background"
                        )
                    }
                    WidgetStyles.Scallop -> {
                        BuildWidgetStyle(stylePathData = StylePathData.ScallopData, bitmap = backgroundBitmap)
                    }
                    WidgetStyles.Polygon -> {
                        BuildWidgetStyle(stylePathData = StylePathData.PolygonData, bitmap = backgroundBitmap)
                    }
                    WidgetStyles.Spikes -> {
                        BuildWidgetStyle(stylePathData = StylePathData.SpikesData, bitmap = backgroundBitmap)
                    }
                    WidgetStyles.Clover -> {
                        BuildWidgetStyle(stylePathData = StylePathData.CloverData, bitmap = backgroundBitmap)
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
                .background(GlanceTheme.colors.widgetBackground)
        ) {
            Text(text = "Couldn't Load Image")
        }
    }

    @Composable
    private fun BuildWidgetStyle(stylePathData: StylePathData, bitmap: Bitmap) {
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
                .fillMaxSize(), //.size(size.width * 1.25f)
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
        val pathData = stringPreferencesKey("path_data")

        const val IMAGE_SHOWCASE_WIDGET_SET_BACKGROUND_ACTION =
            "com.kaii.customwidgets.image_showcase_widget.IMAGE_SHOWCASE_WIDGET_SET_BACKGROUND_ACTION"
        const val EXTRA_IMAGE_SHOWCASE_BACKGROUND_URI =
            "com.kaii.customwidgets.image_showcase_widget.EXTRA_IMAGE_SHOWCASE_BACKGROUND_URI"
        const val EXTRA_IMAGE_SHOWCASE_CHOSEN_STYLE =
            "com.kaii.customwidgets.image_showcase_widget.EXTRA_IMAGE_SHOWCASE_CHOSEN_STYLE"
        const val EXTRA_IMAGE_SHOWCASE_PATH_DATA =
            "com.kaii.customwidgets.image_showcase_widget.EXTRA_IMAGE_SHOWCASE_PATH_DATA"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        // update widget when the right intent is received
        // hacky workaround but too lazy to implement correct method and this works perfectly fine
        if (intent.action == IMAGE_SHOWCASE_WIDGET_SET_BACKGROUND_ACTION) {
            val appWidgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID)
            val chosenStyle = intent.extras?.getSerializable(EXTRA_IMAGE_SHOWCASE_CHOSEN_STYLE, WidgetStyles::class.java)
            val pathData = intent.extras?.getString(EXTRA_IMAGE_SHOWCASE_PATH_DATA)
            val backgroundUri =
                intent.extras?.getParcelable(EXTRA_IMAGE_SHOWCASE_BACKGROUND_URI, Uri::class.java)

            setImageShowCaseWidgetBackground(context, appWidgetId, chosenStyle, pathData, backgroundUri)
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
        nullablePathData: String?,
        backgroundUri: Uri?
    ) {
        MainScope().launch {
            val manager = GlanceAppWidgetManager(context)
            val uriString = backgroundUri.toString()
            val style = nullableStyle ?: WidgetStyles.RoundedSquare
            Log.d(IMAGE_SHOWCASE_WIDGET_TAG, "URI of picked image is: $uriString")
           	Log.d(IMAGE_SHOWCASE_WIDGET_TAG, "path data: $nullablePathData")

            val glanceID = manager.getGlanceIdBy(appWidgetId ?: return@launch)
            glanceID.let {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                    pref.toMutablePreferences().apply {
                        this[backgroundUriString] = uriString
                        this[appWidgetIdInt] = appWidgetId
                        this[chosenStyle] = style.ordinal
                        this[pathData] = nullablePathData ?: "null"
                    }
                }
                glanceAppWidget.update(context, it)
            }
        }
    }
}




