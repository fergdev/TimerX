package com.timerx.ui.run

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.timerx.domain.TimerState
import com.timerx.domain.timeFormatted
import com.timerx.ui.common.AnimatedNumber
import com.timerx.ui.common.CustomIcons
import com.timerx.ui.common.KeepScreenOn
import com.timerx.ui.common.SetStatusBarColor
import com.timerx.ui.common.contrastColor
import kotlinx.coroutines.delay
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.BackHandler
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
private val CORNER_ICON_SIZE = 48.dp

private const val CROSS_FADE_DURATION = 600

@Composable
fun RunScreen(timerId: String, navigateUp: () -> Unit) {
    val viewModel: RunViewModel =
        koinViewModel(vmClass = RunViewModel::class) { parametersOf(timerId) }
    val state by viewModel.state.collectAsState()

    if (state.destroyed) {
        navigateUp()
    }

    val backgroundColor = if (state.timerState == TimerState.Paused) {
        MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f)
            .compositeOver(state.backgroundColor)
    } else {
        state.backgroundColor
    }

    val animatedColor by animateColorAsState(
        backgroundColor,
        animationSpec = tween(CROSS_FADE_DURATION)
    )

    KeepScreenOn()
    SetStatusBarColor(animatedColor)

    RunView(
        backgroundColor = animatedColor,
        interactions = viewModel.interactions,
        state = state,
        navigateUp = navigateUp
    )
}

@Composable
private fun RunView(
    backgroundColor: Color,
    interactions: RunViewModel.Interactions,
    state: RunScreenState,
    navigateUp: () -> Unit,
) {
    var controlsVisible by remember { mutableStateOf(false) }
    var touchCounter by remember { mutableIntStateOf(1) }

    if (state.timerState == TimerState.Paused) {
        controlsVisible = true
    }

    LaunchedEffect(touchCounter) {
        delay(CONTROLS_HIDE_DELAY)
        if (state.timerState != TimerState.Paused) {
            controlsVisible = false
        }
    }

    BackHandler(state.timerState != TimerState.Finished) {
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
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        val contrastDisplayColor = backgroundColor.contrastColor()
        Column(
            modifier = Modifier.padding(16.dp)
                .safeDrawingPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(controlsVisible) {
                TopControls(
                    interactions,
                    contrastDisplayColor,
                    state.volume,
                    state.vibrationEnabled
                ) {
                    controlsVisible = true
                    touchCounter++
                }
            }
            Spacer(modifier = Modifier.weight(1f))

            TimerInformation(
                state,
                contrastDisplayColor,
                interactions,
            )

            Spacer(modifier = Modifier.weight(1f))

            AnimatedVisibility(controlsVisible) {
                BottomControls(state, navigateUp, contrastDisplayColor, interactions) {
                    controlsVisible = true
                    touchCounter++
                }
            }
        }
    }
}

@Composable
private fun TimerInformation(
    state: RunScreenState,
    displayColor: Color,
    interactions: RunViewModel.Interactions,
) {
    if (state.timerState == TimerState.Finished) {
        Text(
            text = stringResource(Res.string.finished).uppercase(),
            style = typography.displayLarge,
            color = displayColor
        )
    } else {

        state.index?.let {
            Crossfade(
                targetState = state.index,
                animationSpec = tween(durationMillis = CROSS_FADE_DURATION)
            ) {
                Text(
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    text = state.index,
                    color = displayColor,
                    style = typography.displaySmall,
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        AnimatedNumber(
            value = state.time,
            textStyle = typography.displayLarge,
            color = displayColor,
            formatter = { it.timeFormatted() },
        )
        Spacer(modifier = Modifier.height(24.dp))
        Crossfade(
            targetState = state.name,
            animationSpec = tween(durationMillis = CROSS_FADE_DURATION)
        ) {
            Text(
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                text = it,
                style = typography.displaySmall,
                color = displayColor
            )
        }
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
    interactions: RunViewModel.Interactions,
    displayColor: Color,
    volume: Float,
    vibrationEnabled: Boolean,
    incrementTouchCounter: () -> Unit,
) {
    Row {
        IconButton(onClick = {
            incrementTouchCounter()
            interactions.previousInterval()
        }) {
            Icon(
                modifier = Modifier.size(CORNER_ICON_SIZE),
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.back),
                tint = displayColor
            )
        }
        Slider(
            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            value = volume,
            valueRange = 0f..1f,
            onValueChange = {
                interactions.updateVolume(it)
            },
            colors = SliderDefaults.colors(
                thumbColor = displayColor,
                activeTrackColor = displayColor,
                inactiveTrackColor = displayColor.copy(alpha = 0.3F)
            )
        )
        IconButton(modifier = Modifier.padding(horizontal = 8.dp), onClick = {
            incrementTouchCounter()
            interactions.updateVibrationEnabled(vibrationEnabled.not())
        }) {
            Icon(
                modifier = Modifier.size(CORNER_ICON_SIZE),
                imageVector = CustomIcons.vibration(),
                contentDescription = stringResource(Res.string.next),
                tint = if (vibrationEnabled) displayColor else displayColor.copy(alpha = 0.5f)
            )
        }
        IconButton(onClick = {
            incrementTouchCounter()
            interactions.nextInterval()
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
    state: RunScreenState,
    navigateUp: () -> Unit,
    displayColor: Color,
    interactions: RunViewModel.Interactions,
    incrementTouchCounter: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (state.timerState == TimerState.Paused || state.timerState == TimerState.Finished) {
            IconButton(onClick = { navigateUp() }) {
                Icon(
                    modifier = Modifier.size(CORNER_ICON_SIZE),
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.close),
                    tint = displayColor
                )
            }
        } else {
            // Empty box to align timer name
            Box(modifier = Modifier.size(CORNER_ICON_SIZE))
        }
        Text(
            text = state.timerName,
            style = typography.bodyLarge,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            color = displayColor
        )
        TogglePlayButton(state, interactions, displayColor, incrementTouchCounter)
    }
}

@Composable
private fun TogglePlayButton(
    state: RunScreenState,
    interactions: RunViewModel.Interactions,
    displayColor: Color,
    incrementTouchCounter: () -> Unit,
) {
    when (state.timerState) {
        TimerState.Running -> {
            IconButton(onClick = interactions.pause) {
                Icon(
                    modifier = Modifier.size(CORNER_ICON_SIZE),
                    imageVector = CustomIcons.pause(),
                    contentDescription = stringResource(Res.string.pause),
                    tint = displayColor
                )
            }
        }

        TimerState.Paused -> {
            IconButton(onClick = {
                interactions.play()
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

        TimerState.Finished -> {
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
