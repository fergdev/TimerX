package com.timerx.ui.create

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.timerx.domain.FinalCountDown
import com.timerx.domain.TimerInterval
import com.timerx.domain.timeFormatted
import com.timerx.ui.common.BeepSelector
import com.timerx.ui.common.ColorPickerModalBottomSheet
import com.timerx.ui.common.CustomIcons
import com.timerx.ui.common.NumberIncrement
import com.timerx.ui.common.RevealDirection
import com.timerx.ui.common.RevealSwipe
import com.timerx.ui.common.TMenuItemIcon
import com.timerx.ui.common.UnderlinedField
import com.timerx.ui.common.VibrationSelector
import com.timerx.ui.common.lightDisplayColor
import com.timerx.ui.common.rainbow
import com.timerx.ui.common.rememberRevealState
import com.timerx.ui.common.reset
import com.timerx.ui.create.CreateScreenIntent.DuplicateInterval
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalColor
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalCountUp
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalManualNext
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalName
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalSkipOnLastSet
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pro.respawn.flowmvi.api.IntentReceiver
import sh.calvin.reorderable.ReorderableScope
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.copy
import timerx.shared.generated.resources.count_down
import timerx.shared.generated.resources.count_up
import timerx.shared.generated.resources.delete
import timerx.shared.generated.resources.finish
import timerx.shared.generated.resources.interval_name
import timerx.shared.generated.resources.manual_next
import timerx.shared.generated.resources.settings
import timerx.shared.generated.resources.skip_on_last_set

private const val COLOR_ANIMATION_DURATION = 400

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun IntentReceiver<CreateScreenIntent>.CreateIntervalContent(
    interval: TimerInterval,
    canSkipOnLastSet: Boolean,
    scope: ReorderableScope,
) {
    val backgroundColor by animateColorAsState(
        interval.color.lightDisplayColor(),
        animationSpec = tween(COLOR_ANIMATION_DURATION)
    )
    val contrastColor = MaterialTheme.colorScheme.onSurface
    val revealState = rememberRevealState(200.dp, setOf(RevealDirection.EndToStart))
    val coroutineScope = rememberCoroutineScope()
    val hideReveal = {
        coroutineScope.launch {
            revealState.reset()
        }
    }
    RevealSwipe(
        state = revealState,
        backgroundCardEndColor = MaterialTheme.colorScheme.surface,
        hiddenContentEnd = {
            var settingsBottomSheetVisible by remember { mutableStateOf(false) }
            if (settingsBottomSheetVisible) {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ModalBottomSheet(
                    sheetState = sheetState,
                    onDismissRequest = { settingsBottomSheetVisible = false }
                ) {
                    IntervalSwitches(
                        canSkipOnLastSet,
                        interval,
                    )
                }
            }
            Row {
                var colorPickerVisible by remember { mutableStateOf(false) }
                IconButton(onClick = {
                    colorPickerVisible = true
                    hideReveal()
                }) {
                    TMenuItemIcon(
                        imageVector = CustomIcons.colorFill,
                        contentDescription = "Interval color",
                        tint = rainbow[0]
                    )
                }

                if (colorPickerVisible) {
                    ColorPickerModalBottomSheet(size = 64.dp) {
                        it?.let { intent(UpdateIntervalColor(interval, it)) }
                        colorPickerVisible = false
                    }
                }
                IconButton(onClick = {
                    intent(DuplicateInterval(interval))
                    hideReveal()
                }) {
                    TMenuItemIcon(
                        imageVector = CustomIcons.contentCopy,
                        contentDescription = stringResource(Res.string.copy),
                        tint = rainbow[3]
                    )
                }
                IconButton(onClick = {
                    intent(CreateScreenIntent.DeleteInterval(interval))
                    hideReveal()
                }) {
                    TMenuItemIcon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(Res.string.delete),
                        tint = rainbow[6]
                    )
                }
                IconButton(onClick = {
                    hideReveal()
                    settingsBottomSheetVisible = true
                }) {
                    TMenuItemIcon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = stringResource(Res.string.settings),
                        tint = rainbow[9]
                    )
                }
            }
        }
    ) {
        Surface {
            Row(
                modifier = Modifier
                    .background(backgroundColor, RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UnderlinedField(
                    modifier = Modifier.weight(1f),
                    value = interval.name,
                    maxLines = 1,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleLarge,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    onValueChange = {
                        intent(UpdateIntervalName(interval, it))
                    },
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.interval_name),
                            fontStyle = FontStyle.Italic
                        )
                    },
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface)
                )

                NumberIncrement(
                    modifier = Modifier.weight(1f),
                    value = interval.duration,
                    negativeButtonEnabled = interval.duration > 1,
                    color = contrastColor,
                    textStyle = MaterialTheme.typography.titleLarge,
                    formatter = { it.timeFormatted() },
                ) {
                    intent(CreateScreenIntent.UpdateIntervalDuration(interval, it))
                }
                Box(modifier = Modifier.weight(1f)) {
                    Icon(
                        modifier = with(scope) {
                            Modifier.size(CustomIcons.defaultIconSize)
                                .align(Alignment.TopEnd)
                                .draggableHandle()
                        },
                        imageVector = CustomIcons.dragHandle,
                        contentDescription = stringResource(Res.string.settings),
                        tint = contrastColor
                    )
                }
            }
        }
    }
}

@Composable
private fun IntentReceiver<CreateScreenIntent>.IntervalSwitches(
    canSkipOnLastSet: Boolean,
    interval: TimerInterval,
) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedCard {
            Column(modifier = Modifier.padding(8.dp)) {
                if (canSkipOnLastSet) {
                    val updateIntervalSkipOnLastSet = {
                        intent(UpdateIntervalSkipOnLastSet(interval, interval.skipOnLastSet.not()))
                    }
                    RowSwitch(
                        modifier = Modifier.clickable { updateIntervalSkipOnLastSet() },
                        label = stringResource(Res.string.skip_on_last_set),
                        value = interval.skipOnLastSet
                    ) {
                        updateIntervalSkipOnLastSet()
                    }
                }
                val updateIntervalCountUp = {
                    intent(UpdateIntervalCountUp(interval, interval.countUp.not()))
                }
                RowSwitch(
                    modifier = Modifier.clickable { updateIntervalCountUp() },
                    label = stringResource(Res.string.count_up),
                    value = interval.countUp
                ) {
                    updateIntervalCountUp()
                }
                val updateIntervalManualNext = {
                    intent(UpdateIntervalManualNext(interval, interval.manualNext.not()))
                }
                RowSwitch(
                    modifier = Modifier.clickable { updateIntervalManualNext() },
                    label = stringResource(Res.string.manual_next),
                    value = interval.manualNext
                ) {
                    updateIntervalManualNext()
                }
            }
        }

        OutlinedCard {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = stringResource(Res.string.finish))
                BeepSelector(selected = interval.beep) {
                    intent(CreateScreenIntent.UpdateIntervalBeep(interval, it))
                }
                VibrationSelector(selected = interval.vibration) {
                    intent(CreateScreenIntent.UpdateIntervalVibration(interval, it))
                }
            }
        }

        OutlinedCard {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = stringResource(Res.string.count_down))
                IntervalCountDown(interval.finalCountDown) {
                    intent(CreateScreenIntent.UpdateIntervalFinalCountDown(interval, it))
                }
            }
        }
    }
}

@Composable
private fun IntervalCountDown(
    finalCountDown: FinalCountDown,
    onUpdate: (FinalCountDown) -> Unit
) {
    Row(
        modifier = Modifier.height(32.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        NumberIncrement(
            value = finalCountDown.duration,
            negativeButtonEnabled = finalCountDown.duration > 0,
            color = MaterialTheme.colorScheme.onSurface,
            formatter = { "$it" }
        ) {
            onUpdate(finalCountDown.copy(duration = it))
        }
    }
    BeepSelector(selected = finalCountDown.beep) {
        onUpdate(finalCountDown.copy(beep = it))
    }
    VibrationSelector(selected = finalCountDown.vibration) {
        onUpdate(finalCountDown.copy(vibration = it))
    }
}

@Composable
private fun RowSwitch(
    modifier: Modifier = Modifier,
    label: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
