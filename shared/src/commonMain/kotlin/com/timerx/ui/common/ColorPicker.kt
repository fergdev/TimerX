package com.timerx.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.timerx.ui.theme.presetColors

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun ColorPickerModalBottomSheet(
    onUpdate: (Color?) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = { onUpdate(null) }) {
        ColorsFlowRow{
            onUpdate(it)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ColorsFlowRow(onClick: (Color) -> Unit) {
    FlowRow(modifier = Modifier.fillMaxWidth().wrapContentWidth()) {
        presetColors.forEach { color ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(32.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(color)
                    .clickable { onClick(color) }
            )
        }
    }
}
