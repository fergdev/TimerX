package com.timerx.ui.create

import ColorPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.timerx.domain.FinalCountDown
import com.timerx.domain.TimerInterval
import com.timerx.domain.timeFormatted
import com.timerx.ui.CustomIcons
import com.timerx.ui.common.BeepPicker
import com.timerx.ui.common.NumberIncrement
import org.jetbrains.compose.resources.stringResource
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.alert
import timerx.shared.generated.resources.copy
import timerx.shared.generated.resources.count_up
import timerx.shared.generated.resources.delete
import timerx.shared.generated.resources.down
import timerx.shared.generated.resources.final_count_down
import timerx.shared.generated.resources.interval
import timerx.shared.generated.resources.manual_next
import timerx.shared.generated.resources.skip_on_last_set
import timerx.shared.generated.resources.up

@Composable
internal fun Interval(
    interval: TimerInterval,
    canSkipOnLastSet: Boolean,
    interactions: CreateViewModel.Interactions
) {
    ElevatedCard {
        Column(
            modifier = Modifier
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

            NumberIncrement(
                value = interval.duration,
                negativeButtonEnabled = interval.duration > 1,
                formatter = { it.timeFormatted() },
            ) {
                interactions.interval.update.updateDuration(
                    interval,
                    it
                )
            }

            IntervalColorSwitches(interval, interactions)

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

@Composable
private fun IntervalColorSwitches(
    interval: TimerInterval,
    interactions: CreateViewModel.Interactions,
) {
    var colorPickerVisible by remember { mutableStateOf(false) }
    Box(modifier = Modifier.size(48.dp)
        .background(interval.color)
        .clickable {
            colorPickerVisible = true
        })

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

    var alertPickerVisible by remember { mutableStateOf(false) }
    if (alertPickerVisible) {
        BeepPicker {
            alertPickerVisible = false
            it?.let {
                interactions.interval.update.updateAlert(interval, it)
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth().clickable { alertPickerVisible = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(Res.string.alert))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = interval.beep.displayName)
    }

    FinalCountDownRow(interval.finalCountDown) {
        interactions.interval.update.updateFinalCountDown(interval, it)
    }
}

@Composable
private fun FinalCountDownRow(
    finalCountDown: FinalCountDown,
    update: (FinalCountDown) -> Unit
) {
    var beepPickerVisible by remember { mutableStateOf(false) }
    if (beepPickerVisible) {
        BeepPicker {
            beepPickerVisible = false
            it?.let {
                update(FinalCountDown(finalCountDown.duration, it))
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth().clickable { beepPickerVisible = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(Res.string.final_count_down))
        Spacer(modifier = Modifier.weight(1f))
        NumberIncrement(
            value = finalCountDown.duration,
            negativeButtonEnabled = finalCountDown.duration > 0,
            positiveButtonEnabled = true
        ) {
            update(FinalCountDown(it, finalCountDown.beep))
        }
        Text(
            modifier = Modifier.clickable {
                beepPickerVisible = true
            },
            text = finalCountDown.beep.displayName
        )
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
