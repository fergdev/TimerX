package com.timerx.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timerx.beep.Beep

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeepPicker(onSelected: (Beep?) -> Unit) {
    ModalBottomSheet(onDismissRequest = { onSelected(null) }) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Beep.entries.forEach {
                Text(
                    modifier = Modifier.clickable {
                        onSelected(it)
                    },
                    text = it.displayName
                )
            }
        }
    }
}

@Composable
fun BeepSelector(
    modifier: Modifier = Modifier,
    selected: Beep,
    onSelected: (Beep) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(modifier = modifier.then(Modifier.horizontalScroll(scrollState))) {
        Beep.entries.forEach {
            FilterChip(
                selected = it == selected,
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = { onSelected(it) },
                label = { Text(text = it.displayName) }
            )
        }
    }
}
