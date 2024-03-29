@file:OptIn(ExperimentalResourceApi::class)

package com.timerx.ui.run

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.timerx.domain.timeFormatted
import com.timerx.ui.CustomIcons
import com.timerx.ui.KeepScreenOn
import com.timerx.ui.SetStatusBarColor
import com.timerx.ui.common.AnimatedNumber
import com.timerx.ui.run.RunViewModel.TimerState.Finished
import com.timerx.ui.run.RunViewModel.TimerState.Paused
import com.timerx.ui.run.RunViewModel.TimerState.Running
import kotlinx.coroutines.delay
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.BackHandler
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.back
import timerx.shared.generated.resources.close
import timerx.shared.generated.resources.finished
import timerx.shared.generated.resources.next
import timerx.shared.generated.resources.pause
import timerx.shared.generated.resources.play
import timerx.shared.generated.resources.restart

private const val CONTROLS_HIDE_DELAY = 3000L
private val CORNER_ICON_SIZE = 1000.dp

@Composable
fun RunScreen(timerId: String, navigateUp: () -> Unit) {
    val viewModel: RunViewModel =
        koinViewModel(vmClass = RunViewModel::class) { parametersOf(timerId) }
    val state by viewModel.state.collectAsState()
    val displayColor = displayColor(state.backgroundColor)

    KeepScreenOn()
    SetStatusBarColor(state.backgroundColor)

    var controlsVisible by remember { mutableStateOf(false) }
    var touchCounter by remember { mutableIntStateOf(1) }

    LaunchedEffect(touchCounter) {
        delay(CONTROLS_HIDE_DELAY)
        if (state.timerState == Running) {
            controlsVisible = false
        }
    }

    BackHandler(true) {
        controlsVisible = true
        touchCounter++
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) {
                controlsVisible = true
                touchCounter++
            }
            .background(state.backgroundColor),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(controlsVisible) {
                TopControls(viewModel, displayColor) {
                    touchCounter++
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            TimerInformation(
                state,
                displayColor,
                viewModel.interactions,
            )

            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(controlsVisible) {
                BottomControls(state, navigateUp, displayColor, viewModel.interactions) {
                    touchCounter++
                }
            }
        }
    }
}

@Composable
private fun TimerInformation(
    state: RunViewModel.RunState,
    displayColor: Color,
    interactions: RunViewModel.Interactions,
) {
    if (state.timerState == Finished) {
        Text(
            text = stringResource(Res.string.finished),
            style = typography.displayLarge,
            color = displayColor
        )
    } else {
        if (state.setRepetitionCount != 1) {
            Text(
                text = "${state.setRepetitionCount - state.repetitionIndex}",
                color = displayColor,
                style = typography.displaySmall,
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        AnimatedNumber(
            value = if (state.displayCountAsUp) state.elapsed
            else state.intervalDuration - state.elapsed,
            style = typography.displayLarge,
            color = displayColor,
            formatter = { it.timeFormatted() },
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = state.intervalName.uppercase(),
            style = typography.displaySmall,
            color = displayColor
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (state.manualNext) {
            Button(onClick = { interactions.onManualNext() }) {
                Text(text = stringResource(Res.string.next))
            }
        }
    }
}

@Composable
private fun TopControls(
    viewModel: RunViewModel,
    displayColor: Color,
    incrementTouchCounter: () -> Unit
) {
    Row {
        IconButton(onClick = {
            incrementTouchCounter()
            viewModel.interactions.previousInterval()
        }) {
            Icon(
                modifier = Modifier.size(CORNER_ICON_SIZE),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.back),
                tint = displayColor
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = {
            incrementTouchCounter()
            viewModel.interactions.nextInterval()
        }) {
            Icon(
                modifier = Modifier.size(CORNER_ICON_SIZE),
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(Res.string.next),
                tint = displayColor
            )
        }
    }
}

@Composable
private fun BottomControls(
    state: RunViewModel.RunState,
    navigateUp: () -> Unit,
    displayColor: Color,
    interactions: RunViewModel.Interactions,
    incrementTouchCounter: () -> Unit
) {
    Row {
        if (state.timerState == Paused || state.timerState == Finished) {
            IconButton(onClick = { navigateUp() }) {
                Icon(
                    modifier = Modifier.size(CORNER_ICON_SIZE),
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.close),
                    tint = displayColor
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        when (state.timerState) {
            Running -> {
                IconButton(onClick = { interactions.togglePlayState() }) {
                    Icon(
                        modifier = Modifier.size(CORNER_ICON_SIZE),
                        imageVector = CustomIcons.pause(),
                        contentDescription = stringResource(Res.string.pause),
                        tint = displayColor
                    )
                }
            }

            Paused -> {
                IconButton(onClick = {
                    interactions.togglePlayState()
                    incrementTouchCounter()
                }) {
                    Icon(
                        modifier = Modifier.size(CORNER_ICON_SIZE),
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(Res.string.play),
                        tint = displayColor
                    )
                }
            }

            Finished -> {
                IconButton(onClick = {
                    interactions.restartTimer()
                    incrementTouchCounter()
                }) {
                    Icon(
                        modifier = Modifier.size(CORNER_ICON_SIZE),
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(Res.string.restart),
                        tint = displayColor
                    )
                }
            }
        }
    }
}

private const val HALF_LUMINANCE = 0.5F
fun displayColor(backgroundColor: Color): Color {
    return if (isColorDark(backgroundColor)) Color.White else Color.Black
}

fun isColorDark(color: Color): Boolean {
    return color.luminance() < HALF_LUMINANCE
}