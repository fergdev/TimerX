package com.timerx.ui.create

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType.Companion.LongPress
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.timerx.domain.timeFormatted
import com.timerx.sound.Beep
import com.timerx.ui.common.AnimatedNumber
import com.timerx.ui.common.BeepSelector
import com.timerx.ui.common.ColorPickerModalBottomSheet
import com.timerx.ui.common.CustomIcons
import com.timerx.ui.common.TCard
import com.timerx.ui.common.TIcon
import com.timerx.ui.common.TScaffold
import com.timerx.ui.common.TTopBar
import com.timerx.ui.common.VibrationSelector
import com.timerx.ui.common.branded
import com.timerx.ui.common.lightDisplayColor
import com.timerx.ui.create.CreateScreenIntent.AddSet
import com.timerx.ui.create.CreateScreenIntent.Save
import com.timerx.ui.create.CreateScreenIntent.SwapSet
import com.timerx.ui.create.CreateScreenIntent.UpdateFinishBeep
import com.timerx.ui.create.CreateScreenIntent.UpdateFinishColor
import com.timerx.ui.create.CreateScreenIntent.UpdateFinishVibration
import com.timerx.ui.create.CreateScreenIntent.UpdateTimerName
import com.timerx.ui.theme.Animation
import com.timerx.vibration.Vibration
import org.jetbrains.compose.resources.stringResource
import pro.respawn.flowmvi.api.IntentReceiver
import pro.respawn.flowmvi.compose.dsl.DefaultLifecycle
import pro.respawn.flowmvi.compose.dsl.subscribe
import sh.calvin.reorderable.ReorderableColumn
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.add
import timerx.shared.generated.resources.cannot_create_empty_timer
import timerx.shared.generated.resources.create
import timerx.shared.generated.resources.edit
import timerx.shared.generated.resources.finish_color
import timerx.shared.generated.resources.ok
import timerx.shared.generated.resources.save
import timerx.shared.generated.resources.timer_name
import timerx.shared.generated.resources.timer_name_required

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateContent(createComponent: CreateComponent) {
    with(createComponent) {
        var emptyTimerDialogVisible by remember { mutableStateOf(false) }
        val state by subscribe(DefaultLifecycle) {
            when (it) {
                is CreateAction.TimerUpdated ->
                    createComponent.onTimerUpdated(it.timerId)

                is CreateAction.EmptyTimerAction ->
                    emptyTimerDialogVisible = true
            }
        }
        val appBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        TScaffold(
            topBar = {
                TTopBar(
                    scrollBehavior = appBarScrollBehavior,
                    title = stringResource(
                        if (state.isEditing) Res.string.edit
                        else Res.string.create
                    ).branded(),
                    onNavigationIconClick = createComponent::onBackClicked,
                    actions = { TopAppBarActions() },
                )
            },
            content = { paddingValues ->
                val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()
                val cutoutPadding = WindowInsets.displayCutout.asPaddingValues()
                val layoutDirection = LocalLayoutDirection.current
                Box(
                    modifier = Modifier.padding(
                        start = systemBarsPadding.calculateStartPadding(layoutDirection)
                            .coerceAtLeast(cutoutPadding.calculateStartPadding(layoutDirection)),
                        end = systemBarsPadding.calculateEndPadding(layoutDirection)
                            .coerceAtLeast(cutoutPadding.calculateEndPadding(layoutDirection))
                    )
                        .widthIn(max = 600.dp)
                        .align(Alignment.TopCenter),
                ) {
                    CreateContent(
                        state,
                        appBarScrollBehavior.nestedScrollConnection,
                        paddingValues
                    )
                }
            }
        )

        if (emptyTimerDialogVisible) {
            Dialog(onDismissRequest = { emptyTimerDialogVisible = false }) {
                TCard(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = stringResource(Res.string.cannot_create_empty_timer))
                    Button(onClick = { emptyTimerDialogVisible = false }) {
                        Text(text = stringResource(Res.string.ok))
                    }
                }
            }
        }
    }
}

@Composable
private fun IntentReceiver<Save>.TopAppBarActions() {
    IconButton(onClick = { intent(Save) }) {
        TIcon(
            imageVector = Icons.Default.Done,
            contentDescription = stringResource(Res.string.save)
        )
    }
}

@Composable
private fun IntentReceiver<UpdateTimerName>.TimerNameTextField(
    timerNameState: TimerNameState
) {
    TCard(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            value = timerNameState.name,
            maxLines = 1,
            onValueChange = { intent(UpdateTimerName(it)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            placeholder = { Text(text = stringResource(Res.string.timer_name)) },
            label = { Text(text = stringResource(Res.string.timer_name)) },
            isError = timerNameState.isError,
            supportingText = {
                if (timerNameState.isError) {
                    Text(text = stringResource(Res.string.timer_name_required))
                }
            }
        )
    }
}

@Composable
private fun IntentReceiver<CreateScreenIntent>.CreateContent(
    state: CreateScreenState,
    nestedScrollConnection: NestedScrollConnection,
    paddingValues: PaddingValues
) {
    val scrollState by mutableStateOf(rememberScrollState())
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
            .nestedScroll(nestedScrollConnection)
            .verticalScroll(scrollState)
    ) {
        Spacer(Modifier.height(paddingValues.calculateTopPadding().plus(8.dp)))
        TimerNameTextField(state.timerNameState)
        SetColumn(state)
        FinalTimeRow(state)
        FinishControls(
            finishColor = state.finishColor,
            finishBeep = state.finishBeep,
            finishVibration = state.finishVibration,
            canVibrate = state.canVibrate
        )
        Spacer(
            Modifier.height(
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
        )
    }
}

@Composable
private fun IntentReceiver<AddSet>.FinalTimeRow(
    state: CreateScreenState
) {
    TCard(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) {
        Box(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp).fillMaxWidth()) {
            FilledIconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = { intent(AddSet) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(Res.string.add)
                )
            }
            AnimatedNumber(
                modifier = Modifier.align(Alignment.TopCenter),
                value = state.sets.length().timeFormatted(),
                textStyle = MaterialTheme.typography.displayMedium
            )
        }
    }
}

@Composable
private fun IntentReceiver<CreateScreenIntent>.SetColumn(state: CreateScreenState) {
    val hapticFeedback = LocalHapticFeedback.current
    ReorderableColumn(
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
        list = state.sets,
        onSettle = { from, to ->
            intent(SwapSet(from, to))
        },
        onMove = {
            hapticFeedback.performHapticFeedback(LongPress)
        },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) { _, set, _ ->
        key(set.id) {
            CreateSetContent(timerSet = set, canVibrate = state.canVibrate, reorderableScope = this)
        }
    }
}

@Composable
private fun IntentReceiver<CreateScreenIntent>.FinishControls(
    finishColor: Color,
    finishBeep: Beep,
    finishVibration: Vibration,
    canVibrate: Boolean
) {
    TCard(
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
    ) {
        FinishColorPicker(finishColor)
        BeepSelector(
            modifier = Modifier.padding(horizontal = 16.dp),
            selected = finishBeep
        ) {
            intent(UpdateFinishBeep(it))
        }
        if (canVibrate) {
            VibrationSelector(
                modifier = Modifier.padding(16.dp),
                selected = finishVibration
            ) {
                intent(UpdateFinishVibration(it))
            }
        }
    }
}

@Composable
private fun IntentReceiver<CreateScreenIntent>.FinishColorPicker(finishColor: Color) {
    var colorPickerVisible by remember { mutableStateOf(false) }
    if (colorPickerVisible) {
        ColorPickerModalBottomSheet(size = 64.dp) {
            it?.let { intent(UpdateFinishColor(it)) }
            colorPickerVisible = false
        }
    }
    val backgroundColor by animateColorAsState(
        targetValue = finishColor.lightDisplayColor(),
        animationSpec = tween(Animation.fast)
    )
    Box(
        modifier = Modifier.clickable { colorPickerVisible = true }
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.medium
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(Res.string.finish_color))
            Spacer(modifier = Modifier.weight(1f))
            TIcon(
                imageVector = CustomIcons.colorFill,
                contentDescription = "Finish color",
            )
        }
    }
}
