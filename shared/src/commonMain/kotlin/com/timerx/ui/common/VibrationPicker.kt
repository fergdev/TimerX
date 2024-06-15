package com.timerx.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timerx.vibration.Vibration


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VibrationPicker(onSelected: (Vibration?) -> Unit) {
    ModalBottomSheet(onDismissRequest = { onSelected(null) }) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Vibration.entries.forEach {
                Text(modifier = Modifier.clickable {
                    onSelected(it)
                }, text = it.displayName)
            }
        }
    }
}
