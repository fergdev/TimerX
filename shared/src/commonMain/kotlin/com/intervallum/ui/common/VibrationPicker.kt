package com.intervallum.ui.common

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.intervallum.vibration.Vibration

@Composable
fun VibrationSelector(
    selected: Vibration,
    modifier: Modifier = Modifier,
    onSelect: (Vibration) -> Unit
) {
    Row(modifier = modifier.horizontalScroll(rememberScrollState())) {
        Vibration.entries.forEach {
            FilterChip(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .testTag(it.name),
                selected = it == selected,
                onClick = { onSelect(it) },
                label = { Text(text = it.displayName) }
            )
        }
    }
}
