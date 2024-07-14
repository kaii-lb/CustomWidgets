package com.kaii.customwidgets.image_showcase_widget.selection_list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kaii.customwidgets.image_showcase_widget.ImageShowCaseConfigurationActivity

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectableItem(selectionOption: SelectionOptions, onOptionClicked: (SelectionOptions) -> Unit) {
    val itemShape = itemShapes(index = selectionOption.index)
    val clip = itemShape.first
    val inClip = itemShape.second
    val rotation = itemShape.third

    Column(
        modifier = Modifier
            .fillMaxSize(1f)
            .padding(4.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                if (selectionOption.selected) {
                    val primaryColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    primaryColor
                } else {
                    Color.Transparent
                }
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .size(230.dp)
                .padding(8.dp)
                .rotate(rotation)
                .background(Color.Transparent),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize(1f) //.size(size.width * 1.25f)
                    .padding(8.dp)
                    .clip(clip)
                    .clickable(true, onClick = { onOptionClicked(selectionOption) })
                    .background(MaterialTheme.colorScheme.background),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(1f) //.size(size.width * 1.25f)
                        .padding(8.dp)
                        .clip(inClip)
                        .background(MaterialTheme.colorScheme.primary),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    // val inputStream = applicationContext.assets.open("background.jpg")
                    // val bitmap = BitmapFactory.decodeStream(inputStream).asImageBitmap()
                    // Image(
                    //     bitmap = bitmap,
                    //     contentDescription = "just a sample image",
                    //     contentScale = ContentScale.Crop,
                    //     modifier = Modifier.then(inClip)
                    // )
                }
            }
        }

        Text(text = selectionOption.option,
            maxLines = 1,
            textAlign = TextAlign.Center, // add font weight
            modifier = Modifier
                .height(24.dp),
        )
    }
}
