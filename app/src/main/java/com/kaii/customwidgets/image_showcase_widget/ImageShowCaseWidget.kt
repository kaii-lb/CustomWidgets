package com.kaii.customwidgets.image_showcase_widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.LocalContext
import androidx.glance.LocalSize
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
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.unit.ColorProvider
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.Alignment
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

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

			// add something for 3 cell sizes, its fucks up
            GlanceTheme {
                Content(backgroundUri, appWidgetId)
            }
        }
    }

    @Composable
    private fun Content(backgroundUri: String, appWidgetId: Int) {
        //val size = LocalSize.current
        val uri = Uri.parse(backgroundUri)
        val context = LocalContext.current.applicationContext
        if(appWidgetId == 123456789) return
		
		val filePath = context.getExternalFilesDir("backgrounds")?.path + "/image_$appWidgetId.png"
		val folder = File(context.getExternalFilesDir("backgrounds")?.path ?: return)
		if (!folder.exists()) {
			folder.mkdir()
		}
		val file = File(filePath)
		
        val inputStream = context.contentResolver.openInputStream(uri)

        val displayMetrics = Resources.getSystem().displayMetrics
        val maxMemoryUsage = displayMetrics.widthPixels * displayMetrics.heightPixels * 4 * 1.5f
        println("MAX MEMORY USAGE FOR BITMAP: $maxMemoryUsage")

		lateinit var holderBitmap: Bitmap

        if (inputStream == null && !file.exists()) {
            ZeroState()
            return
        }
        else if (inputStream != null) {
        	holderBitmap = BitmapFactory.decodeStream(inputStream)
   	        inputStream.close()	
        }
        
		
        try {
            val fileOutputStream = FileOutputStream(file)
            holderBitmap.compress(Bitmap.CompressFormat.PNG, 25, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: Exception) {
            android.util.Log.e("ERROR SAVING FILE", e.message ?: "file save failed")
            // show a toast saying change pic failed
        }

        val options = BitmapFactory.Options()
        options.inSampleSize = 2 // play with this to get best resolution for screen size

		// this is failing miserably, change it
        if (holderBitmap.byteCount >= maxMemoryUsage) {
        	println("TOO MUCH MEMORY USAGE")
            options.inSampleSize = 4
        }
        val backgroundBitmap = BitmapFactory.decodeFile(filePath, options)

        if (backgroundBitmap == null) {
            ZeroState()
            return // and show a toast
        }

		Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .padding(4.dp)
                .cornerRadius(20.dp)
                .background(ColorProvider(Color.Transparent)),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
        ) {
            Row(
                modifier = GlanceModifier
                    .fillMaxSize() //.size(size.width * 1.25f)
                    .cornerRadius(24.dp)
                    .padding(8.dp)
                    .clickable(actionStartActivity<ImageShowCaseConfigurationActivity>())
                    .background(GlanceTheme.colors.widgetBackground),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally,

                ) {
		            Image (
		                provider = ImageProvider(backgroundBitmap),
		                contentDescription = "background image that the user selected",
		                contentScale = ContentScale.Crop,
		                modifier = GlanceModifier
		                    .defaultWeight()
                            .fillMaxSize() //.size(size.width * 1.15f)
		                    .cornerRadius(16.dp)
		            )
		        }
        }
        println("UPDATED BACKGROUND")
    }

    @Composable
    private fun ZeroState() {
        Column (
            modifier = GlanceModifier
                .appWidgetBackground()
                .background(GlanceTheme.colors.widgetBackground)
        ) {
            Text(text = "Couldn't Load Image")
        }
    }

    // override on delete and delete image_$appWidgetId.png
}

class ImageShowCaseWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ImageShowCaseWidget()

    companion object {
        val backgroundUriString = stringPreferencesKey("background_uri")
        val appWidgetIdInt = intPreferencesKey("app_widget_id")

        const val IMAGE_SHOWCASE_WIDGET_SET_BACKGROUND_ACTION =
            "com.kaii.customwidgets.image_showcase_widget.IMAGE_SHOWCASE_WIDGET_SET_BACKGROUND_ACTION"
        const val EXTRA_IMAGE_SHOWCASE_BACKGROUND_URI =
            "com.kaii.customwidgets.image_showcase_widget.EXTRA_IMAGE_SHOWCASE_BACKGROUND_URI"
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == IMAGE_SHOWCASE_WIDGET_SET_BACKGROUND_ACTION) {
            val appWidgetId = intent.extras?.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID)
            val backgroundUri =
                intent.extras?.getParcelable(EXTRA_IMAGE_SHOWCASE_BACKGROUND_URI, Uri::class.java)

            setImageShowCaseWidgetBackground(context, appWidgetId, backgroundUri)
        }
    }

    private fun setImageShowCaseWidgetBackground(
        context: Context,
        appWidgetId: Int?,
        backgroundUri: Uri?
    ) {
        MainScope().launch {
            val manager = GlanceAppWidgetManager(context)
            val uriString = backgroundUri.toString()

            println("URI STRING: $uriString")

            val glanceID = manager.getGlanceIdBy(appWidgetId ?: return@launch)
            glanceID.let {
                updateAppWidgetState(context, PreferencesGlanceStateDefinition, it) { pref ->
                    pref.toMutablePreferences().apply {
                        this[backgroundUriString] = uriString
                        this[appWidgetIdInt] = appWidgetId
                    }
                }
                glanceAppWidget.update(context, it)
            }
        }
    }
}




