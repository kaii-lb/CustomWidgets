package com.kaii.customwidgets.image_showcase_widget.styles

import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import kotlin.math.max
import android.graphics.RectF

fun RoundedPolygon.getBounds() = calculateBounds().let { Rect(it[0], it[1], it[2], it[3]) }

class RoundedPolygonShape(
    private val polygonPath: android.graphics.Path,
    private val rotationDegrees: Float = 0f,
    private var matrix: Matrix = Matrix()
) : Shape {
    private var path = Path()

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        path.rewind()
        matrix.reset()

        val path = polygonPath.asComposePath()
		val bounds = RectF()
		polygonPath.computeBounds(bounds, true)        

        val maxDimension = max(bounds.width(), bounds.height())
        
       	matrix.scale(size.width / maxDimension, size.height / maxDimension)
        matrix.translate(-bounds.left, -bounds.top)
        matrix.rotateZ(rotationDegrees)

        path.transform(matrix)
        return  Outline.Generic(path)
    }
}
