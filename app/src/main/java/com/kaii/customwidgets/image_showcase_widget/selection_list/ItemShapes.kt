package com.kaii.customwidgets.image_showcase_widget.selection_list

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asComposePath
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.star
import androidx.graphics.shapes.pill
import androidx.graphics.shapes.rectangle
import com.kaii.customwidgets.image_showcase_widget.styles.RoundedPolygonShape
import com.kaii.customwidgets.image_showcase_widget.ImageShowCaseConfigurationActivity
import com.kaii.customwidgets.image_showcase_widget.styles.getPathDataForStyle
import com.kaii.customwidgets.image_showcase_widget.styles.WidgetStyles

@Composable
fun itemShapes(index: Int) : Pair<RoundedPolygonShape, RoundedPolygonShape> {
    when (index) {
        0 -> { // rounded square
			val pathPair = getPathDataForStyle(WidgetStyles.RoundedSquare)
			
            return Pair(RoundedPolygonShape(pathPair.first), RoundedPolygonShape(pathPair.second))
        }
        1 -> { // circle
			val pathPair = getPathDataForStyle(WidgetStyles.Circle)

            return Pair(RoundedPolygonShape(pathPair.first), RoundedPolygonShape(pathPair.second))
        }
        2 -> { // scallop
			val pathPair = getPathDataForStyle(WidgetStyles.Scallop)

            return Pair(RoundedPolygonShape(pathPair.first), RoundedPolygonShape(pathPair.second))
        }
        3 -> { // polygon
			val pathPair = getPathDataForStyle(WidgetStyles.Polygon)

            return Pair(RoundedPolygonShape(pathPair.first, 0f), RoundedPolygonShape(pathPair.second, 0f))
        }
        4 -> { // spikes
			val pathPair = getPathDataForStyle(WidgetStyles.Spikes)

            return Pair(RoundedPolygonShape(pathPair.first), RoundedPolygonShape(pathPair.second))
        }
        5 -> { // clover
			val pathPair = getPathDataForStyle(WidgetStyles.Clover)

            return Pair(RoundedPolygonShape(pathPair.first), RoundedPolygonShape(pathPair.second))
        }
        6 -> { // rectangle
			val pathPair = getPathDataForStyle(WidgetStyles.Rectangle)

            return Pair(RoundedPolygonShape(pathPair.first, 180f), RoundedPolygonShape(pathPair.second, 180f))
        }
		else -> { // pill
   			val pathPair = getPathDataForStyle(WidgetStyles.Pill)

            return Pair(RoundedPolygonShape(pathPair.first, 180f), RoundedPolygonShape(pathPair.second, 180f))
        }        
    }
}
