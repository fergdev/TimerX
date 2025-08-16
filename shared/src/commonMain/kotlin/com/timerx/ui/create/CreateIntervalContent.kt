package com.timerx.ui.create

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.timerx.domain.timeFormatted
import com.timerx.ui.common.BeepSelector
import com.timerx.ui.common.ColorPickerModalBottomSheet
import com.timerx.ui.common.CustomIcons
import com.timerx.ui.common.NumberIncrement
import com.timerx.ui.common.RevealDirection
import com.timerx.ui.common.RevealSwipe
import com.timerx.ui.common.TIcon
import com.timerx.ui.common.UnderlinedTextField
import com.timerx.ui.common.VibrationSelector
import com.timerx.ui.common.lightDisplayColor
import com.timerx.ui.common.rememberRevealState
import com.timerx.ui.common.reset
import com.timerx.ui.create.CreateScreenIntent.DuplicateInterval
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalColor
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalCountUp
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalFinalCountDown
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalManualNext
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalName
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalSkipOnLastSet
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalTextToSpeech
import kotlinx.coroutines.Job
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

@Composable
internal fun IntentReceiver<CreateScreenIntent>.CreateIntervalContent(
    interval: CreateTimerInterval,
    canSkipOnLastSet: Boolean,
    canVibrate: Boolean,
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
        modifier = Modifier.padding(4.dp),
        state = revealState,
        hiddenContentEnd = {
            HiddenIntervalControls(
                canVibrate = canVibrate,
                canSkipOnLastSet = canSkipOnLastSet,
                interval = interval,
                contrastColor = contrastColor,
                hideReveal = hideReveal
            )
        }
    ) {
        Row(
            modifier = Modifier
                .background(backgroundColor, MaterialTheme.shapes.medium)
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UnderlinedTextField(
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
                value = interval.duration,
                negativeButtonEnabled = interval.duration > 1,
                color = contrastColor,
                textStyle = MaterialTheme.typography.titleLarge,
                formatter = { it.timeFormatted() }
            ) {
                intent(CreateScreenIntent.UpdateIntervalDuration(interval, it))
            }
            Box(modifier = Modifier.weight(1f)) {
                TIcon(
                    modifier = with(scope) {
                        Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IntentReceiver<CreateScreenIntent>.HiddenIntervalControls(
    canVibrate: Boolean,
    canSkipOnLastSet: Boolean,
    interval: CreateTimerInterval,
    contrastColor: Color,
    hideReveal: () -> Job,
) {
    var settingsBottomSheetVisible by remember { mutableStateOf(false) }
    if (settingsBottomSheetVisible) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { settingsBottomSheetVisible = false }
        ) {
            IntervalSettings(
                canVibrate = canVibrate,
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
            TIcon(
                imageVector = CustomIcons.colorFill,
                contentDescription = "Interval color",
                tint = contrastColor
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
            TIcon(
                imageVector = CustomIcons.contentCopy,
                contentDescription = stringResource(Res.string.copy),
                tint = contrastColor
            )
        }
        IconButton(onClick = {
            intent(CreateScreenIntent.DeleteInterval(interval))
            hideReveal()
        }) {
            TIcon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(Res.string.delete),
                tint = contrastColor
            )
        }
        IconButton(onClick = {
            hideReveal()
            settingsBottomSheetVisible = true
        }) {
            TIcon(
                imageVector = Icons.Filled.Settings,
                contentDescription = stringResource(Res.string.settings),
                tint = contrastColor
            )
        }
    }
}

@Composable
private fun IntentReceiver<CreateScreenIntent>.IntervalSettings(
    canVibrate: Boolean,
    canSkipOnLastSet: Boolean,
    interval: CreateTimerInterval,
) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        IntervalBehaviourControls(canSkipOnLastSet, interval)
        IntervalAlertControls(canVibrate, interval)
        IntervalFinalCountDown(interval, canVibrate)
    }
}

@Composable
private fun IntentReceiver<CreateScreenIntent>.IntervalBehaviourControls(
    canSkipOnLastSet: Boolean,
    interval: CreateTimerInterval
) {
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
}

@Composable
private fun IntentReceiver<CreateScreenIntent>.IntervalFinalCountDown(
    interval: CreateTimerInterval,
    canVibrate: Boolean
) {
    with(interval.finalCountDown) {
        OutlinedCard {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = stringResource(Res.string.count_down))
                Row(
                    modifier = Modifier.height(32.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    NumberIncrement(
                        value = duration,
                        negativeButtonEnabled = duration > 0,
                        color = MaterialTheme.colorScheme.onSurface,
                    ) {
                        intent(
                            UpdateIntervalFinalCountDown(
                                interval = interval,
                                finalCountDown = copy(duration = it)
                            )
                        )
                    }
                }
                BeepSelector(selected = beep) {
                    intent(
                        UpdateIntervalFinalCountDown(
                            interval = interval,
                            finalCountDown = copy(beep = it)
                        )
                    )
                }
                if (canVibrate) {
                    VibrationSelector(selected = vibration) {
                        intent(
                            UpdateIntervalFinalCountDown(
                                interval = interval,
                                finalCountDown = copy(vibration = it)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IntentReceiver<CreateScreenIntent>.IntervalAlertControls(
    canVibrate: Boolean,
    interval: CreateTimerInterval
) {
    OutlinedCard {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(Res.string.finish))
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    selected = interval.textToSpeech.not(),
                    onClick = {
                        intent(
                            UpdateIntervalTextToSpeech(
                                interval,
                                interval.textToSpeech.not()
                            )
                        )
                    },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 0,
                        count = 2
                    ),
                    icon = {}
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                }
                SegmentedButton(
                    selected = interval.textToSpeech,
                    onClick = {
                        intent(
                            UpdateIntervalTextToSpeech(
                                interval,
                                interval.textToSpeech.not()
                            )
                        )
                    },
                    icon = {},
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 1,
                        count = 2
                    ),
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                }
            }
            AnimatedVisibility(interval.textToSpeech.not()) {
                BeepSelector(selected = interval.beep) {
                    intent(CreateScreenIntent.UpdateIntervalBeep(interval, it))
                }
            }
            if (canVibrate) {
                VibrationSelector(selected = interval.vibration) {
                    intent(CreateScreenIntent.UpdateIntervalVibration(interval, it))
                }
            }
        }
    }
}

@Composable
private fun RowSwitch(
    label: String,
    value: Boolean,
    modifier: Modifier = Modifier,
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
