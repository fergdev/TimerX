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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import com.timerx.ui.common.VibrationSelector
import com.timerx.ui.common.contrastColor
import org.jetbrains.compose.resources.stringResource
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.copy
import timerx.shared.generated.resources.count_up
import timerx.shared.generated.resources.delete
import timerx.shared.generated.resources.down
import timerx.shared.generated.resources.interval
import timerx.shared.generated.resources.manual_next
import timerx.shared.generated.resources.settings
import timerx.shared.generated.resources.skip_on_last_set
import timerx.shared.generated.resources.up

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Interval(
    interval: TimerInterval,
    canSkipOnLastSet: Boolean,
    interactions: CreateViewModel.Interactions
) {
    val contrastColor = interval.color.contrastColor()
    Column(
        modifier = Modifier
            .background(interval.color)
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = interval.name,
            maxLines = 1,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            label = { Text(text = stringResource(Res.string.interval)) },
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

        Row(verticalAlignment = Alignment.CenterVertically) {
            IntervalColorSwitches(interval, interactions, contrastColor)
            Spacer(modifier = Modifier.weight(1F))

            NumberIncrement(
                value = interval.duration,
                negativeButtonEnabled = interval.duration > 1,
                color = contrastColor,
                formatter = { it.timeFormatted() },
            ) {
                interactions.interval.update.updateDuration(
                    interval,
                    it
                )
            }
            Spacer(modifier = Modifier.weight(1F))
            IconButton(onClick = { settingsBottomSheetVisible = true }) {
                Icon(
                    modifier = Modifier.size(CustomIcons.defaultIconSize),
                    imageVector = Icons.Filled.Settings,
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

    Text(text = "Finish")
    BeepSelector(selected = interval.beep) {
        interactions.interval.update.updateBeep(interval, it)
    }
    VibrationSelector(selected = interval.vibration) {
        interactions.interval.update.updateVibration(interval, it)
    }

    Text(text = "Countdown")
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
        IconButton(onClick = { interactions.interval.moveDown(interval) }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(Res.string.down)
            )
        }
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
        IconButton(onClick = { interactions.interval.moveUp(interval) }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = stringResource(Res.string.up)
            )
        }
    }
}
