package com.timerx.android.run

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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.timerx.android.R
import com.timerx.android.run.RunViewModel.TimerState.Finished
import com.timerx.android.run.RunViewModel.TimerState.Paused
import com.timerx.android.run.RunViewModel.TimerState.Running
import org.koin.androidx.compose.getViewModel

@Composable
fun RunScreen(navController: NavHostController, viewModel: RunViewModel = getViewModel()) {
    val state by viewModel.state.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                IconButton(onClick = { viewModel.previousInterval() }) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.back),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(2f))
                IconButton(onClick = { viewModel.nextInterval() }) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = stringResource(R.string.next),
                        tint = Color.White
                    )
                }
            }

            TText(text = state.timerName)
            Spacer(modifier = Modifier.weight(2f))

            if (state.timerState == Finished) {
                TText(text = stringResource(id = R.string.finished))
            } else {
                if (state.setRepetitionCount != 1L) {
                    TText(text = "${state.setRepetitionCount - state.setRepetition}")
                }
                Spacer(modifier = Modifier.height(24.dp))
                TText(text = state.intervalName)
                Spacer(modifier = Modifier.height(24.dp))
                TText(text = "${state.intervalDuration - state.elapsed} ")
            }

            Spacer(modifier = Modifier.weight(2f))

            Row {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.weight(2f))
                when (state.timerState) {
                    Running -> {
                        IconButton(onClick = { viewModel.toggleState() }) {
                            Icon(
                                modifier = Modifier.size(48.dp),
                                imageVector = ImageVector.vectorResource(id = R.drawable.pause_24),
                                contentDescription = stringResource(R.string.pause),
                                tint = Color.White
                            )
                        }
                    }

                    Paused -> {
                        IconButton(onClick = { viewModel.toggleState() }) {
                            Icon(
                                modifier = Modifier.size(48.dp),
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = stringResource(R.string.play),
                                tint = Color.White
                            )
                        }
                    }

                    Finished -> {
                        IconButton(onClick = { viewModel.initTimer() }) {
                            Icon(
                                modifier = Modifier.size(48.dp),
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.refresh),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TText(text: String) {
    Text(
        text = text, style = typography.headlineLarge, color = Color.White
    )
}