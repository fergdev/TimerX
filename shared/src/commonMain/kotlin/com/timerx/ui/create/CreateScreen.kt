package com.timerx.ui.create

import ColorPicker
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.timerx.domain.length
import com.timerx.domain.timeFormatted
import com.timerx.ui.CustomIcons
import com.timerx.ui.common.AnimatedNumber
import com.timerx.ui.common.BeepSelector
import com.timerx.ui.common.UnderlinedField
import com.timerx.ui.common.VibrationSelector
import com.timerx.ui.common.contrastColor
import moe.tlaster.precompose.koin.koinViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import sh.calvin.reorderable.ReorderableColumn
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.add
import timerx.shared.generated.resources.back
import timerx.shared.generated.resources.finish_color

private const val TWO_HUNDRED_SEVENTY_DEG = 270f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CreateScreen(
    timerId: String,
    navigateUp: () -> Unit
) {
    val viewModel: CreateViewModel =
        koinViewModel(vmClass = CreateViewModel::class) { parametersOf(timerId) }

    val state by viewModel.state.collectAsState()
    Surface(modifier = Modifier.navigationBarsPadding()) {
        Column {
            TopAppBar(
                title = {
                    UnderlinedField(
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        value = state.timerName,
                        onValueChange = {
                            viewModel.interactions.updateTimerName(it)
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        textStyle = MaterialTheme.typography.headlineSmall.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    )
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
            )

            Box(modifier = Modifier.fillMaxSize()) {
                CreateContent(state, viewModel.interactions)
            }
        }
    }
}

@Composable
private fun CreateContent(
    state: CreateViewModel.State,
    interactions: CreateViewModel.Interactions
) {
    val scrollState by mutableStateOf(rememberScrollState())
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        ReorderableColumn(
            list = state.sets,
            onSettle = { from, to ->
                interactions.swapSet(from, to)
            },
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) { _, set, _ ->
            key(set.id) {
                Set(
                    timerSet = set,
                    interactions = interactions,
                    this
                )
            }
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            FilledIconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = { interactions.addSet() }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(Res.string.add)
                )
            }
            AnimatedNumber(
                modifier = Modifier.align(Alignment.TopCenter),
                value = state.sets.length(),
                textStyle = MaterialTheme.typography.displayMedium
            ) { it.timeFormatted() }
        }
        Spacer(modifier = Modifier.height(16.dp))
        FinishControls(state, interactions)
    }
}

@Composable
fun FinishControls(
    state: CreateViewModel.State,
    interactions: CreateViewModel.Interactions
) {
    val backgroundColor by animateColorAsState(
        targetValue = state.finishColor,
        animationSpec = tween(400)
    )
    val contrastColor by animateColorAsState(
        targetValue = state.finishColor.contrastColor(),
        animationSpec = tween(400)
    )

    Column(modifier = Modifier.background(backgroundColor)) {
        FinishColorPicker(interactions.updateFinishColor, contrastColor)
        BeepSelector(modifier = Modifier.padding(horizontal = 16.dp), selected = state.finishBeep) {
            interactions.updateFinishAlert(it)
        }
        VibrationSelector(modifier = Modifier.padding(16.dp), selected = state.finishVibration) {
            interactions.updateFinishVibration(it)
        }
    }
}

@Composable
private fun FinishColorPicker(
    updateFinishColor: (Color) -> Unit,
    contrastColor: Color
) {
    var colorPickerVisible by remember { mutableStateOf(false) }
    Box(modifier = Modifier.clickable { colorPickerVisible = true }) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.finish_color),
                color = contrastColor
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                modifier = Modifier.size(CustomIcons.defaultIconSize),
                imageVector = CustomIcons.colorFill(),
                contentDescription = null,
                tint = contrastColor
            )

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
}