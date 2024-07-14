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
fun itemShapes(index: Int) : Triple<RoundedPolygonShape, RoundedPolygonShape, Float> {
    lateinit var clip: RoundedPolygonShape
    lateinit var inClip: RoundedPolygonShape
	var rotation: Float

    when (index) {
        0 -> { // rounded square
            val roundedSquare = RoundedPolygon(
	            numVertices = 4,
	            rounding = CornerRounding(
	                radius = 0.25f
	            )
            )
			clip = RoundedPolygonShape(roundedSquare)
	
            val roundedSquareIn = RoundedPolygon(
                numVertices = 4,
                rounding = CornerRounding(
                    radius = 0.2f
                )
            )
           	inClip = RoundedPolygonShape(roundedSquareIn)

           	rotation = 45f
        }
        1 -> { // circle
            val circle = RoundedPolygon.circle(
                numVertices = 3,
            )
            clip = RoundedPolygonShape(circle)

            val circleIn = RoundedPolygon.circle(
                numVertices = 3,
            )
            inClip = RoundedPolygonShape(circleIn)

            rotation = 0f
        }
        2 -> { // scallop
            val roundedStar = RoundedPolygon.star(
                numVerticesPerRadius = 12,
                rounding = CornerRounding(
                    radius = 0.2f,
                ),
                innerRounding = CornerRounding(
                    radius = 0.2f,
                )
            )
            clip = RoundedPolygonShape(roundedStar)

			val roundedStarIn = RoundedPolygon.star(
			    numVerticesPerRadius = 12,
			    rounding = CornerRounding(
			        radius = 0.2f,
			    ),
			    innerRounding = CornerRounding(
			        radius = 0.2f,
			    )
			)
			inClip = RoundedPolygonShape(roundedStarIn)

			rotation = 0f
        }
        3 -> { // polygon
            val roundedPolygon = RoundedPolygon(
                numVertices = 5,
                rounding = CornerRounding(
                    radius = 0.3f,
                ),
            )
			clip = RoundedPolygonShape(roundedPolygon)

            val roundedPolygonIn = RoundedPolygon(
                numVertices = 5,
                rounding = CornerRounding(
                    radius = 0.3f,
                ),
            )
            inClip = RoundedPolygonShape(roundedPolygonIn)

			rotation = 55f
        }
        4 -> { // spikes
            val roundedStar = RoundedPolygon.star(
                numVerticesPerRadius = 8,
                innerRadius = 0.8f,
                rounding = CornerRounding(
                    radius = 0.15f,
                ),
                innerRounding = CornerRounding(
                    radius = 0.15f,
                )
            )
            clip = RoundedPolygonShape(roundedStar)

            val roundedStarIn = RoundedPolygon.star(
                numVerticesPerRadius = 8,
                innerRadius = 0.8f,
                rounding = CornerRounding(
                    radius = 0.1f,
                ),
                innerRounding = CornerRounding(
                    radius = 0.1f,
                )
            )
            inClip = RoundedPolygonShape(roundedStarIn)

            rotation = 0f
        }
        else -> { // clover
            val roundedClover = RoundedPolygon.star(
                numVerticesPerRadius = 4,
                innerRadius = 0.45f,
                rounding = CornerRounding(
                    radius = 0.4f,
                ),
                innerRounding = CornerRounding(
                    radius = 0.5f,
                ),
            )
			clip = RoundedPolygonShape(roundedClover, 45f)
 
            val roundedCloverIn = RoundedPolygon.star(
                numVerticesPerRadius = 4,
                innerRadius = 0.45f,
                rounding = CornerRounding(
                    radius = 0.35f,
                ),
                innerRounding = CornerRounding(
                    radius = 0.55f,
                )
            )
			inClip = RoundedPolygonShape(roundedCloverIn)

			rotation = 45f
        }
    }

    return Triple(clip, inClip, rotation)
}
