package com.kaii.customwidgets.image_showcase_widget.selection_list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kaii.customwidgets.image_showcase_widget.ImageShowCaseConfigurationActivity
import com.kaii.customwidgets.image_showcase_widget.styles.WidgetStyles

class SelectionOptions (val option: String, val index: Int, var isSquare: Boolean, private var initiallySelected: Boolean) {
    var selected by mutableStateOf(initiallySelected)
}

class SelectionModel {
    private val optionsList = listOf(
        SelectionOptions("Square", 0, true, true),
        SelectionOptions("Circle", 1, false, false),
        SelectionOptions("Scallop", 2, false, false),
        SelectionOptions("Polygon", 3, false, false),
        SelectionOptions("Spikes", 4, false, false),
        SelectionOptions("Clover", 5, false, false),
        SelectionOptions("Rectangle", 6, false, false),
        SelectionOptions("Pill", 7, false, false),
    )

    val options: List<SelectionOptions>
        get() = optionsList

    fun selectionOptionSelected(selectedOption: SelectionOptions) {
        optionsList.forEach { it.selected = false }
        optionsList.find {
            it.option == selectedOption.option
        }?.selected = true

     	ImageShowCaseConfigurationActivity.chosenStyle = WidgetStyles.entries[selectedOption.index]
    }
}

@Composable
fun SingleSelectionList(options: List<SelectionOptions>, onOptionClicked: (SelectionOptions) -> Unit) {
    LazyColumn (
        modifier = Modifier
            .fillMaxSize(1f)
            .padding(16.dp),
    ) {
        items(options) { option ->
            SelectableItem(selectionOption = option, onOptionClicked)
        }
    }
}
