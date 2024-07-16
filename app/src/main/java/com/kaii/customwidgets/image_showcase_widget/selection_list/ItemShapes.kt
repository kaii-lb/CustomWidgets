package com.kaii.customwidgets.image_showcase_widget.selection_list

import androidx.compose.runtime.Composable
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.circle
import androidx.graphics.shapes.star
import androidx.graphics.shapes.pill
import androidx.graphics.shapes.rectangle
import com.kaii.customwidgets.image_showcase_widget.styles.RoundedPolygonShape

@Composable
fun itemShapes(index: Int) : Pair<RoundedPolygonShape, RoundedPolygonShape> {
    when (index) {
        0 -> { // rounded square
//            if (isBackground) com.kaii.customwidgets.R.drawable.image_showcase_square_background
//            else com.kaii.customwidgets.R.drawable.image_showcase_square

            val roundedSquare = RoundedPolygon(
                numVertices = 4,
                rounding = CornerRounding(0.3f),
                centerX = -0.15f,
                centerY = 0.2f,
            )

            val roundedSquareIn = RoundedPolygon(
                numVertices = 4,
                rounding = CornerRounding(0.26f),
                centerX = -0.15f,
                centerY = 0.2f,
            )

            return Pair(RoundedPolygonShape(roundedSquare, 45f, 1.15f), RoundedPolygonShape(roundedSquareIn, 45f, 1.15f))
        }
        1 -> { // circle
//            com.kaii.customwidgets.R.drawable.image_showcase_circle
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

            return Pair(RoundedPolygonShape(circle), RoundedPolygonShape(circleIn))
        }
        2 -> { // scallop
//            com.kaii.customwidgets.R.drawable.image_showcase_scallop
            val scallop = RoundedPolygon.star(
                numVerticesPerRadius = 12,
                rounding = CornerRounding(0.2f),
                innerRounding = CornerRounding(0.15f),
                innerRadius = 0.9f,
                centerX = 0.5f,
                centerY = 0.5f,
            )

            val scallopIn = RoundedPolygon.star(
                numVerticesPerRadius = 12,
                rounding = CornerRounding(0.15f),
                innerRounding = CornerRounding(0.1f),
                innerRadius = 0.9f,
                centerX = 0.5f,
                centerY = 0.5f,
            )

            return Pair(RoundedPolygonShape(scallop), RoundedPolygonShape(scallopIn))
        }
        3 -> { // polygon
//            com.kaii.customwidgets.R.drawable.image_showcase_polygon

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

            return Pair(RoundedPolygonShape(polygon, 55f), RoundedPolygonShape(polygonIn, 55f))
        }
        4 -> { // spikes
//            com.kaii.customwidgets.R.drawable.image_showcase_spikes
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

            return Pair(RoundedPolygonShape(spikes), RoundedPolygonShape(spikesIn))
        }
        5 -> { // clover
//            if (isBackground) com.kaii.customwidgets.R.drawable.image_showcase_clover_background
//            else com.kaii.customwidgets.R.drawable.image_showcase_clover

            val clover = RoundedPolygon.star(
                numVerticesPerRadius = 4,
                rounding = CornerRounding(0.7f),
                innerRounding = CornerRounding(0.5f),
                innerRadius = 0.4f,
                centerX = -0.1f,
                centerY = 0.15f,
            )

            val cloverIn = RoundedPolygon.star(
                numVerticesPerRadius = 4,
                rounding = CornerRounding(0.55f),
                innerRounding = CornerRounding(0.55f),
                innerRadius = 0.4f,
                centerX = -0.101f,
                centerY = 0.1525f,
            )

            return Pair(RoundedPolygonShape(clover, 45f, 1.15f), RoundedPolygonShape(cloverIn, 45f, 1.15f))
        }
        6 -> { // rectangle
        //            if (isBackground) com.kaii.customwidgets.R.drawable.image_showcase_clover_background
        //            else com.kaii.customwidgets.R.drawable.image_showcase_clover
        
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

            return Pair(RoundedPolygonShape(roundedRectangle, 180f), RoundedPolygonShape(roundedRectangleIn, 180f))
        }
		else -> { // pill
			//            if (isBackground) com.kaii.customwidgets.R.drawable.image_showcase_clover_background
			//            else com.kaii.customwidgets.R.drawable.image_showcase_clover

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

            return Pair(RoundedPolygonShape(pill, 180f), RoundedPolygonShape(pillIn, 180f))
        }        
    }
}
