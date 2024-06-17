package com.timerx.ui.create

import ColorPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.timerx.domain.FinalCountDown
import com.timerx.domain.TimerInterval
import com.timerx.domain.timeFormatted
import com.timerx.ui.CustomIcons
import com.timerx.ui.common.BeepSelector
import com.timerx.ui.common.NumberIncrement
import com.timerx.ui.common.UnderlinedTextBox
import com.timerx.ui.common.VibrationSelector
import com.timerx.ui.common.contrastColor
import org.jetbrains.compose.resources.stringResource
import sh.calvin.reorderable.ReorderableScope
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.copy
import timerx.shared.generated.resources.count_down
import timerx.shared.generated.resources.count_up
import timerx.shared.generated.resources.delete
import timerx.shared.generated.resources.finish
import timerx.shared.generated.resources.manual_next
import timerx.shared.generated.resources.settings
import timerx.shared.generated.resources.skip_on_last_set

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Interval(
    interval: TimerInterval,
    canSkipOnLastSet: Boolean,
    interactions: CreateViewModel.Interactions,
    scope: ReorderableScope
) {
    val contrastColor = interval.color.contrastColor()
    Row(
        modifier = Modifier
            .background(interval.color)
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UnderlinedTextBox(
            modifier = Modifier.weight(1f),
            value = interval.name,
            maxLines = 1,
            textStyle = MaterialTheme.typography.titleLarge,
            color = interval.color.contrastColor(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            onValueChange = {
                interactions.interval.update.updateName(
                    interval,
                    it
                )
            }
        )

        var settingsBottomSheetVisible by remember { mutableStateOf(false) }
        if (settingsBottomSheetVisible) {
            ModalBottomSheet(onDismissRequest = { settingsBottomSheetVisible = false }) {
                Column(modifier = Modifier.padding(16.dp)) {
                    IntervalSwitches(
                        canSkipOnLastSet,
                        interval,
                        interactions,
                    )

                    IntervalBottomControls(
                        interactions,
                        interval,
                    )
                }
            }
        }

        NumberIncrement(
            modifier = Modifier.weight(1f),
            value = interval.duration,
            negativeButtonEnabled = interval.duration > 1,
            color = contrastColor,
            textStyle = MaterialTheme.typography.titleLarge,
            formatter = { it.timeFormatted() },
        ) {
            interactions.interval.update.updateDuration(
                interval,
                it
            )
        }
        Row(modifier = Modifier.weight(1f)) {
            IntervalColorSwitches(interval, interactions, contrastColor)
            IconButton(onClick = { settingsBottomSheetVisible = true }) {
                Icon(
                    modifier = Modifier.size(CustomIcons.defaultIconSize),
                    imageVector = Icons.Filled.Settings,
                    contentDescription = stringResource(Res.string.settings),
                    tint = contrastColor
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    modifier = with(scope) {
                        Modifier.size(CustomIcons.defaultIconSize)
                            .draggableHandle()
                    },
                    imageVector = CustomIcons.dragHandle(),
                    contentDescription = stringResource(Res.string.settings),
                    tint = contrastColor
                )
            }
        }
    }
}

@Composable
private fun IntervalColorSwitches(
    interval: TimerInterval,
    interactions: CreateViewModel.Interactions,
    contrastColor: Color
) {
    var colorPickerVisible by remember { mutableStateOf(false) }
    IconButton(onClick = { colorPickerVisible = true }) {
        Icon(
            modifier = Modifier.size(CustomIcons.defaultIconSize),
            imageVector = CustomIcons.colorFill(),
            contentDescription = null,
            tint = contrastColor
        )
    }

    if (colorPickerVisible) {
        ColorPicker {
            if (it != null) {
                interactions.interval.update.updateColor(
                    interval,
                    it
                )
            }
            colorPickerVisible = false
        }
    }
}

@Composable
private fun IntervalSwitches(
    canSkipOnLastSet: Boolean,
    interval: TimerInterval,
    interactions: CreateViewModel.Interactions,
) {
    if (canSkipOnLastSet) {
        RowSwitch(stringResource(Res.string.skip_on_last_set), interval.skipOnLastSet) {
            interactions.interval.update.updateSkipOnLastSet(
                interval,
                it
            )
        }
    }
    RowSwitch(stringResource(Res.string.count_up), interval.countUp) {
        interactions.interval.update.updateCountUp(
            interval,
            it
        )
    }
    RowSwitch(stringResource(Res.string.manual_next), interval.manualNext) {
        interactions.interval.update.updateManualNext(
            interval,
            it
        )
    }

    Text(text = stringResource(Res.string.finish))
    BeepSelector(selected = interval.beep) {
        interactions.interval.update.updateBeep(interval, it)
    }
    VibrationSelector(selected = interval.vibration) {
        interactions.interval.update.updateVibration(interval, it)
    }

    Text(text = stringResource(Res.string.count_down))
    IntervalCountDown(interval.finalCountDown) {
        interactions.interval.update.updateFinalCountDown(interval, it)
    }
}

@Composable
private fun IntervalCountDown(
    finalCountDown: FinalCountDown,
    update: (FinalCountDown) -> Unit
) {
    Row(
        modifier = Modifier.height(32.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        NumberIncrement(
            value = finalCountDown.duration,
            negativeButtonEnabled = finalCountDown.duration > 0,
            color = MaterialTheme.colorScheme.onSurface,
            formatter = { "$it" }) {
            update(finalCountDown.copy(duration = it))
        }
    }
    BeepSelector(selected = finalCountDown.beep) {
        update(finalCountDown.copy(beep = it))
    }
    VibrationSelector(selected = finalCountDown.vibration) {
        update(finalCountDown.copy(vibration = it))
    }
}

@Composable
private fun RowSwitch(label: String, value: Boolean, onValueChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label)
        Spacer(modifier = Modifier.weight(1f))
        Switch(
            checked = value,
            onCheckedChange = onValueChange
        )
    }
}

@Composable
private fun IntervalBottomControls(
    interactions: CreateViewModel.Interactions,
    interval: TimerInterval,
) {
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { interactions.interval.duplicate(interval) }) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = CustomIcons.contentCopy(),
                contentDescription = stringResource(Res.string.copy)
            )
        }
        IconButton(onClick = { interactions.interval.delete(interval) }) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(Res.string.delete)
            )
        }
    }
}
