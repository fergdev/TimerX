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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
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
import com.timerx.ui.run.RunViewModel.TimerState.Finished
import com.timerx.ui.run.RunViewModel.TimerState.Paused
import com.timerx.ui.run.RunViewModel.TimerState.Running
import moe.tlaster.precompose.koin.koinViewModel
import org.koin.core.parameter.parametersOf

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
                        contentDescription = "Back",
                        tint = displayColor
                    )
                }
                Spacer(modifier = Modifier.weight(2f))
                IconButton(onClick = { viewModel.nextInterval() }) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        tint = displayColor
                    )
                }
            }

            TText(text = state.timerName, displayColor)
            Spacer(modifier = Modifier.weight(2f))

            if (state.timerState == Finished) {
                TText(text = "Finished", displayColor)
            } else {
                if (state.setRepetitionCount != 1L) {
                    TText(text = "${state.setRepetitionCount - state.setRepetition}", displayColor)
                }
                Spacer(modifier = Modifier.height(24.dp))
                TText(text = state.intervalName, displayColor)
                Spacer(modifier = Modifier.height(24.dp))
                TText(text = "${state.intervalDuration - state.elapsed} ", displayColor)
            }

            Spacer(modifier = Modifier.weight(2f))

            Row {
                IconButton(onClick = { navigateUp() }) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = displayColor
                    )
                }
                Spacer(modifier = Modifier.weight(2f))
                when (state.timerState) {
                    Running -> {
                        IconButton(onClick = { viewModel.toggleState() }) {
                            Icon(
                                modifier = Modifier.size(48.dp),
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Pause",
                                tint = displayColor
                            )
                        }
                    }

                    Paused -> {
                        IconButton(onClick = { viewModel.toggleState() }) {
                            Icon(
                                modifier = Modifier.size(48.dp),
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = displayColor
                            )
                        }
                    }

                    Finished -> {
                        IconButton(onClick = { viewModel.initTimer() }) {
                            Icon(
                                modifier = Modifier.size(48.dp),
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = displayColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TText(text: String, color: Color) {
    Text(
        text = text,
        style = typography.headlineLarge,
        color = color
    )
}

private fun displayColor(backgroundColor: Color): Color {
    return if (backgroundColor.luminance() > 0.5f) Color.Black else Color.White
}