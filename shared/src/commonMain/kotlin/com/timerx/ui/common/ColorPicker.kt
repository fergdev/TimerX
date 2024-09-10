package com.timerx.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
@OptIn(
    ExperimentalMaterial3Api::class
)
internal fun ColorPicker(
    onUpdate: (Color?) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = { onUpdate(null) }) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ColorPickerBox(Color.Red) { onUpdate(it) }
            ColorPickerBox(Color.Green) { onUpdate(it) }
            ColorPickerBox(Color.Blue) { onUpdate(it) }
            ColorPickerBox(Color.Yellow) { onUpdate(it) }
        }
    }
}

@Composable
private fun ColorPickerBox(color: Color, onClick: (Color) -> Unit) {
    Box(
        modifier = Modifier.size(48.dp).background(color).clickable {
            onClick(color)
        }
    )
}
