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

class SelectionOptions (val option: String, val index: Int, private var initiallySelected: Boolean) {
    var selected by mutableStateOf(initiallySelected)
}

class SelectionModel {
    private val optionsList = listOf(
        SelectionOptions("Squared", 0, true),
        SelectionOptions("Circle", 1, false),
        SelectionOptions("Scallop", 2, false),
        SelectionOptions("Polygon", 3, false),
        SelectionOptions("Spikes", 4, false),
        SelectionOptions("Clover", 5, false),
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
