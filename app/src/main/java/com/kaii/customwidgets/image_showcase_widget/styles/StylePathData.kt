package com.kaii.customwidgets.image_showcase_widget.styles

import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.star
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.rectangle
import androidx.graphics.shapes.pill
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import android.graphics.Path
import android.graphics.Matrix
import android.graphics.RectF
import com.kaii.customwidgets.image_showcase_widget.styles.WidgetStyles

fun getPathDataForStyle(style: WidgetStyles) : Pair<Path, Path> {
	val returnVal = when (style) {
		WidgetStyles.RoundedSquare -> {
            val roundedSquare = RoundedPolygon.rectangle(
                width = 2f,
                height = 2f,
                rounding = CornerRounding(0.36f),
                centerX = -0.15f,
                centerY = 0.2f,
            )

            val roundedSquareIn = RoundedPolygon.rectangle(
                width = 2f,
                height = 2f,
                rounding = CornerRounding(0.32f),
                centerX = -0.15f,
                centerY = 0.2f,
            )
            
			Pair(roundedSquare.toPath(), roundedSquareIn.toPath())
		}
		
		WidgetStyles.Circle -> {
			val circle = RoundedPolygon.circle(
                numVertices = 12,
                centerX = 0.5f,
                centerY = 0.5f,
            )

            val circleIn = RoundedPolygon.circle(
                numVertices = 12,
                centerX = 0.5f,
                centerY = 0.5f,
            )		
		
			Pair(circle.toPath(), circleIn.toPath())
		}
		
		WidgetStyles.Scallop -> {
			val scallop = RoundedPolygon.star(
                numVerticesPerRadius = 12,
                rounding = CornerRounding(0.2f),
                innerRounding = CornerRounding(0.15f),
                innerRadius = 0.9f,
                centerX = 0.0f,
                centerY = 0.0f,
            )

            val scallopIn = RoundedPolygon.star(
                numVerticesPerRadius = 12,
                rounding = CornerRounding(0.15f),
                innerRounding = CornerRounding(0.1f),
                innerRadius = 0.9f,
                centerX = 0.0f,
                centerY = 0.0f,
            )

            Pair(scallop.toPath(), scallopIn.toPath())
		}

		WidgetStyles.Polygon -> {
			val polygon = RoundedPolygon.star(
                numVerticesPerRadius = 5,
                rounding = CornerRounding(0.4f),
                innerRadius = 0.8f,
                centerX = -0.005f,
                centerY = -0.125f,
            )

            val polygonIn = RoundedPolygon.star(
                numVerticesPerRadius = 5,
                rounding = CornerRounding(0.35f),
                innerRadius = 0.8f,
                centerX = -0.005f,
                centerY = -0.135f,
            )

            val matrix = Matrix()
			val rectF = RectF()
			val matrixIn = Matrix()
			val rectFIn = RectF()
			val path = polygon.toPath()
			val pathIn = polygonIn.toPath()

			path.computeBounds(rectF, true)
			matrix.postRotate(-17.5f, rectF.centerX(), rectF.centerY())
			path.transform(matrix)

			pathIn.computeBounds(rectFIn, true)
			matrixIn.postRotate(-17.5f, rectFIn.centerX(), rectFIn.centerY())
			pathIn.transform(matrix)

            Pair(path, pathIn)
		}

		WidgetStyles.Spikes -> {
			val spikes = RoundedPolygon.star(
                numVerticesPerRadius = 8,
                rounding = CornerRounding(0.2f),
                innerRounding = CornerRounding(0.15f),
                innerRadius = 0.8f,
                centerX = 0.5f,
                centerY = 0.5f,
            )

            val spikesIn = RoundedPolygon.star(
                numVerticesPerRadius = 8,
                rounding = CornerRounding(0.15f),
                innerRounding = CornerRounding(0.1f),
                innerRadius = 0.8f,
                centerX = 0.5f,
                centerY = 0.5f,
            )

            Pair(spikes.toPath(), spikesIn.toPath())
		}

		WidgetStyles.Clover -> {
			val clover = RoundedPolygon.star(
                numVerticesPerRadius = 4,
                rounding = CornerRounding(0.335f),
                innerRounding = CornerRounding(0.225f),
                innerRadius = 0.5f,
                centerX = 0.5f,
                centerY = 0.5f,
            )

            val cloverIn = RoundedPolygon.star(
                numVerticesPerRadius = 4,
                rounding = CornerRounding(0.3f),
                innerRounding = CornerRounding(0.175f),
                innerRadius = 0.45f,
                centerX = 0.5f,
                centerY = 0.5f,
            )			

            val matrix = Matrix()
			val rectF = RectF()
			val matrixIn = Matrix()
			val rectFIn = RectF()
			val path = clover.toPath()
			val pathIn = cloverIn.toPath()

			path.computeBounds(rectF, true)
			matrix.postRotate(45f, rectF.centerX(), rectF.centerY())
			path.transform(matrix)

			pathIn.computeBounds(rectFIn, true)
			matrixIn.postRotate(45f, rectFIn.centerX(), rectFIn.centerY())
			pathIn.transform(matrix)

			Pair(path, pathIn)
		}

		WidgetStyles.Rectangle -> {
			val roundedRectangle = RoundedPolygon.rectangle(
                width = 2f,
                height = 1f,
                rounding = CornerRounding(0.3f),
                centerX = 0.0f,
                centerY = -0.3f,
            )

            val roundedRectangleIn = RoundedPolygon.rectangle(
                width = 2f,
                height = 0.93f,
                rounding = CornerRounding(0.26f),
                centerX = 0.0f,
                centerY = -0.32f,
            )
            		
			Pair(roundedRectangle.toPath(), roundedRectangleIn.toPath())
		}

		WidgetStyles.Pill -> {
			val pill = RoundedPolygon.pill(
                width = 2f,
                height = 1f,
                centerX = 0.0f,
                centerY = -0.3f,
            )

            val pillIn = RoundedPolygon.pill(
                width = 2f,
                height = 0.925f,
                centerX = 0.0f,
                centerY = -0.3215f,
            )		
            
			Pair(pill.toPath(), pillIn.toPath())
		}
	}

	return returnVal
}
