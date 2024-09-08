package com.timerx.ui.common

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.timerx.vibration.Vibration

@Composable
fun VibrationSelector(
    modifier: Modifier = Modifier,
    selected: Vibration,
    onSelected: (Vibration) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(modifier = modifier.then(Modifier.horizontalScroll(scrollState))) {
        Vibration.entries.forEach {
            FilterChip(
                selected = it == selected,
                modifier = Modifier.padding(horizontal = 4.dp),
                onClick = { onSelected(it) },
                label = { Text(text = it.displayName) }
            )
        }
    }
}
