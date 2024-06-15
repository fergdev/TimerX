package com.timerx.ui.create

import ColorPicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.timerx.beep.Beep
import com.timerx.domain.length
import com.timerx.domain.timeFormatted
import com.timerx.ui.common.AnimatedNumber
import com.timerx.ui.common.BeepPicker
import com.timerx.ui.common.VibrationPicker
import com.timerx.vibration.Vibration
import kotlinx.coroutines.delay
import moe.tlaster.precompose.koin.koinViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.add
import timerx.shared.generated.resources.back
import timerx.shared.generated.resources.finish_alert
import timerx.shared.generated.resources.finish_color
import timerx.shared.generated.resources.vibration

private const val TWO_HUNDRED_SEVENTY_DEG = 270f
private const val FOCUS_REQUEST_DELAY: Long = 300

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateScreen(
    timerId: String,
    navigateUp: () -> Unit
) {
    val viewModel: CreateViewModel =
        koinViewModel(vmClass = CreateViewModel::class) { parametersOf(timerId) }

    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Surface(modifier = Modifier.navigationBarsPadding()) {
        Column {
            TopAppBar(
                title = {
                    TimerName(state, viewModel.interactions)
                },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.rotate(TWO_HUNDRED_SEVENTY_DEG),
                        onClick = {
                            viewModel.interactions.save()
                            navigateUp()
                        }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                CreateContent(state, viewModel)
            }
        }
    }
}

@Composable
private fun TimerName(
    state: CreateViewModel.State,
    interactions: CreateViewModel.Interactions
) {
    val focusRequester = remember { FocusRequester() }
    var text by remember { mutableStateOf(TextFieldValue(state.timerName)) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    BasicTextField(
        modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
        maxLines = 1,
        value = text,
        onValueChange = {
            text = it
            interactions.updateTimerName(it.text)
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        keyboardActions = KeyboardActions(onAny = {
            keyboardController?.hide()
            text = TextFieldValue(text.text)
            focusManager.clearFocus()
        })
    )
    LaunchedEffect(Unit) {
        delay(FOCUS_REQUEST_DELAY)
        text = text.copy(selection = TextRange(0, text.text.length))
        focusRequester.requestFocus()
    }
}

@Composable
private fun CreateContent(
    state: CreateViewModel.State,
    viewModel: CreateViewModel,
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(state.sets, key = { it.hashCode() }) {
            Set(
                timerSet = it,
                viewModel.interactions
            )
        }
        item {
            Box(
                modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                FilledIconButton(onClick = { viewModel.interactions.addSet() }) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(Res.string.add)
                    )
                }
            }
        }
        item {
            Box(
                modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                AnimatedNumber(state.sets.length()) { it.timeFormatted() }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            FinishColorPicker(state.finishColor, viewModel.interactions.updateFinishColor)
        }
        item {
            FinishAlertPicker(state.finishAlert, viewModel.interactions.updateFinishAlert)
        }
        item {
            FinishVibrationPicker(
                state.finishVibration,
                viewModel.interactions.updateFinishVibration
            )
        }
    }
}

@Composable
private fun FinishVibrationPicker(
    finishVibration: Vibration,
    updateFinishVibration: (Vibration) -> Unit
) {
    var vibrationPickerVisible by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
            .clickable { vibrationPickerVisible = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(Res.string.vibration))
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier)
        Text(text = finishVibration.displayName)
        if (vibrationPickerVisible) {
            VibrationPicker {
                if (it != null) {
                    updateFinishVibration(it)
                }
                vibrationPickerVisible = false
            }
        }
    }
}

@Composable
private fun FinishColorPicker(
    finishColor: Color,
    updateFinishColor: (Color) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(Res.string.finish_color))
        Spacer(modifier = Modifier.width(16.dp))
        var colorPickerVisible by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier.size(48.dp).background(finishColor)
                .clickable {
                    colorPickerVisible = true
                })

        if (colorPickerVisible) {
            ColorPicker {
                if (it != null) {
                    updateFinishColor(it)
                }
                colorPickerVisible = false
            }
        }
    }
}

@Composable
private fun FinishAlertPicker(
    beep: Beep,
    updateFinishAlert: (Beep) -> Unit
) {
    var alertPickerVisible by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
            .clickable { alertPickerVisible = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(Res.string.finish_alert))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = beep.displayName)

        if (alertPickerVisible) {
            BeepPicker {
                it?.let { updateFinishAlert(it) }
                alertPickerVisible = false
            }
        }
    }
}
