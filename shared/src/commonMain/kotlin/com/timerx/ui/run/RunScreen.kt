@file:OptIn(ExperimentalResourceApi::class)

package com.timerx.ui.run

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import com.timerx.CustomIcons
import com.timerx.ui.run.RunViewModel.TimerState.Finished
import com.timerx.ui.run.RunViewModel.TimerState.Paused
import com.timerx.ui.run.RunViewModel.TimerState.Running
import moe.tlaster.precompose.koin.koinViewModel
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

@Composable
fun RunScreen(timerId: String, navigateUp: () -> Unit) {
    val viewModel: RunViewModel =
        koinViewModel(vmClass = RunViewModel::class) { parametersOf(timerId) }
    val state by viewModel.state.collectAsState()
    val displayColor = displayColor(state.backgroundColor)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(state.backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                IconButton(onClick = { viewModel.previousInterval() }) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.back),
                        tint = displayColor
                    )
                }
                Spacer(modifier = Modifier.weight(2f))
                IconButton(onClick = { viewModel.nextInterval() }) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = stringResource(Res.string.next),
                        tint = displayColor
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))

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
                Text(
                    text = state.intervalName.uppercase(),
                    style = typography.displayLarge,
                    color = displayColor
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = if (state.displayCountAsUp) "${state.elapsed}"
                    else "${state.intervalDuration - state.elapsed} ",
                    style = typography.displaySmall,
                    color = displayColor
                )
                Spacer(modifier = Modifier.height(24.dp))
                if (state.manualNext) {
                    Button(onClick = { viewModel.onManualNext() }) {
                        Text(text = stringResource(Res.string.next))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Row {
                if (state.timerState == Paused || state.timerState == Finished) {
                    IconButton(onClick = { navigateUp() }) {
                        Icon(
                            modifier = Modifier.size(48.dp),
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.close),
                            tint = displayColor
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(2f))
                when (state.timerState) {
                    Running -> {
                        IconButton(onClick = { viewModel.toggleState() }) {
                            Icon(
                                modifier = Modifier.size(48.dp),
                                imageVector = CustomIcons.pause(),
                                contentDescription = stringResource(Res.string.pause),
                                tint = displayColor
                            )
                        }
                    }

                    Paused -> {
                        IconButton(onClick = { viewModel.toggleState() }) {
                            Icon(
                                modifier = Modifier.size(48.dp),
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = stringResource(Res.string.play),
                                tint = displayColor
                            )
                        }
                    }

                    Finished -> {
                        IconButton(onClick = { viewModel.initTimer() }) {
                            Icon(
                                modifier = Modifier.size(48.dp),
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(Res.string.restart),
                                tint = displayColor
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun displayColor(backgroundColor: Color): Color {
    return if (backgroundColor.luminance() > 0.5f) Color.Black else Color.White
}