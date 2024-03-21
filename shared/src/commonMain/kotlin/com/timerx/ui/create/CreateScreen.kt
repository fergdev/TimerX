package com.timerx.ui.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import com.timerx.domain.formatted
import com.timerx.domain.length
import moe.tlaster.precompose.koin.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    timerId: String,
    navigateUp: () -> Unit
) {
    val viewModel: CreateViewModel =
        koinViewModel(vmClass = CreateViewModel::class) { parametersOf(timerId) }

    val state by viewModel.state.collectAsState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Create timer") },
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.rotate(270F),
                        onClick = { navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }, floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.save()
                navigateUp()
            }) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Create"
                )
            }
        }

    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                        value = state.timerName,
                        label = { Text(text = "Timer name") },
                        onValueChange = { viewModel.updateTimerName(it) })
                }
                items(state.sets) {
                    Set(
                        timerSet = it,
                        addInterval = viewModel::addInterval,

                        deleteSet = viewModel::deleteSet,
                        duplicateSet = viewModel::duplicateSet,

                        moveSetUp = viewModel::moveSetUp,
                        moveSetDown = viewModel::moveSetDown,

                        deleteInterval = viewModel::deleteInterval,
                        duplicateInterval = viewModel::duplicateInterval,

                        moveIntervalUp = viewModel::moveIntervalUp,
                        moveIntervalDown = viewModel::moveIntervalDown,

                        updateRepetitions = viewModel::updateRepetitions,
                        updateIntervalDuration = viewModel::updateIntervalDuration,
                        updateIntervalName = viewModel::updateIntervalName,
                    )
                }
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                    ) {
                        FilledIconButton(onClick = { viewModel.addSet() }) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add"
                            )
                        }
                    }
                }
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Total ${state.sets.length().formatted()}")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun Set(
    timerSet: TimerSet,
    addInterval: (TimerSet) -> Unit,

    deleteSet: (TimerSet) -> Unit,
    duplicateSet: (TimerSet) -> Unit,

    moveSetUp: (TimerSet) -> Unit,
    moveSetDown: (TimerSet) -> Unit,

    deleteInterval: (TimerInterval) -> Unit,
    duplicateInterval: (TimerInterval) -> Unit,

    moveIntervalUp: (TimerInterval) -> Unit,
    moveIntervalDown: (TimerInterval) -> Unit,

    updateRepetitions: (TimerSet, Long) -> Unit,
    updateIntervalDuration: (TimerInterval, Long) -> Unit,
    updateIntervalName: (TimerInterval, String) -> Unit
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Absolute.SpaceBetween
            ) {
                OutlinedIconButton(onClick = { moveSetDown(timerSet) }) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Down"
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Repetitions")
                    NumberIncrement(
                        value = timerSet.repetitions,
                        onChange = { updateRepetitions(timerSet, it) })
                }
                OutlinedIconButton(onClick = { moveSetUp(timerSet) }) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Up"
                    )
                }
            }

            timerSet.intervals.forEach { interval ->
                Interval(
                    interval = interval,

                    moveIntervalUp = moveIntervalUp,
                    moveIntervalDown = moveIntervalDown,

                    deleteInterval = deleteInterval,
                    duplicateInterval = duplicateInterval,
                    updateDuration = updateIntervalDuration,
                    updateName = updateIntervalName
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(text = timerSet.length().formatted())

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilledTonalIconButton(onClick = { duplicateSet(timerSet) }) {
                    Icon(
                        imageVector = Icons.Default.MailOutline,
                        contentDescription = "Duplicate"
                    )
                }
                FilledTonalIconButton(onClick = { addInterval(timerSet) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                }
                FilledTonalIconButton(onClick = { deleteSet(timerSet) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
            }
        }
    }
}

@Composable
private fun Interval(
    interval: TimerInterval,
    moveIntervalUp: (TimerInterval) -> Unit,
    moveIntervalDown: (TimerInterval) -> Unit,
    deleteInterval: (TimerInterval) -> Unit,
    duplicateInterval: (TimerInterval) -> Unit,
    updateDuration: (TimerInterval, Long) -> Unit,
    updateName: (TimerInterval, String) -> Unit
) {
    ElevatedCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = interval.name,
                label = { Text(text = "Interval") },
                onValueChange = { updateName(interval, it) })

            NumberIncrement(
                value = interval.duration, formatter = ::timeFormatter
            ) {
                updateDuration(interval, it)
            }

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { moveIntervalDown(interval) }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Down"
                    )
                }
                IconButton(onClick = { duplicateInterval(interval) }) {
                    Icon(
                        imageVector = Icons.Default.MailOutline,
                        contentDescription = "Duplicate"
                    )
                }
                IconButton(onClick = { deleteInterval(interval) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                }
                IconButton(onClick = { moveIntervalUp(interval) }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Up"
                    )
                }
            }
        }
    }
}

private fun timeFormatter(time: Long): String {
    return time.formatted()
}

@Composable
private fun NumberIncrement(
    value: Long,
    formatter: (Long) -> String = { "$it" },
    onChange: (Long) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onChange(value - 1) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Minus"
            )
        }
        Text(text = formatter(value))
        IconButton(onClick = { onChange(value + 1) }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )
        }
    }
}