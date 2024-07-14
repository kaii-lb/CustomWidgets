package com.kaii.customwidgets.image_showcase_widget.selection_list

import android.graphics.RectF
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Matrix
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.star
import com.kaii.customwidgets.image_showcase_widget.styles.RoundedPolygonShape
import com.kaii.customwidgets.image_showcase_widget.styles.getBounds

@Composable
fun itemShapes(index: Int) : Int {
    val id = when (index) {
        0 -> { // rounded square
            com.kaii.customwidgets.R.drawable.image_showcase_square
        }
        1 -> { // circle
            com.kaii.customwidgets.R.drawable.image_showcase_circle
        }
        2 -> { // scallop
            com.kaii.customwidgets.R.drawable.image_showcase_scallop
        }
        3 -> { // polygon
            com.kaii.customwidgets.R.drawable.image_showcase_polygon
        }
        4 -> { // spikes
            com.kaii.customwidgets.R.drawable.image_showcase_spikes
        }
        else -> { // clover
            com.kaii.customwidgets.R.drawable.image_showcase_clover
        }
    }

    return id
}
