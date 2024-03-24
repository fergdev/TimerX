package com.timerx.ui.create

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import com.timerx.domain.timeFormatted
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
                        updateIntervalColor = viewModel::updateIntervalColor,

                        updateSkipOnLastSet = viewModel::updateSkipOnLastSet,
                        updateCountUp = viewModel::updateCountUp,
                        updateManualNext = viewModel::updateManualNext
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
                        Text(text = "Total ${state.sets.length().timeFormatted()}")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Finish color")
                        Spacer(modifier = Modifier.width(16.dp))
                        var colorPickerVisible by remember { mutableStateOf(false) }
                        Box(
                            modifier = Modifier.size(48.dp).background(state.finishColor)
                                .clickable {
                                    colorPickerVisible = true
                                })

                        if (colorPickerVisible) {
                            ColorPicker {
                                if (it != null) {
                                    viewModel.onFinishColor(it)
                                }
                                colorPickerVisible = false
                            }
                        }
                    }
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

    updateRepetitions: (TimerSet, Int) -> Unit,
    updateIntervalDuration: (TimerInterval, Int) -> Unit,
    updateIntervalName: (TimerInterval, String) -> Unit,
    updateIntervalColor: (TimerInterval, Color) -> Unit,

    updateSkipOnLastSet: (TimerInterval, Boolean) -> Unit,
    updateCountUp: (TimerInterval, Boolean) -> Unit,
    updateManualNext: (TimerInterval, Boolean) -> Unit
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
                    canSkipOnLastSet = timerSet.repetitions > 1,

                    moveIntervalUp = moveIntervalUp,
                    moveIntervalDown = moveIntervalDown,

                    deleteInterval = deleteInterval,
                    duplicateInterval = duplicateInterval,
                    updateDuration = updateIntervalDuration,
                    updateName = updateIntervalName,
                    updateColor = updateIntervalColor,
                    updateSkipOnLastSet = updateSkipOnLastSet,
                    updateCountUp = updateCountUp,
                    updateManualNext = updateManualNext
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(text = timerSet.length().timeFormatted())

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
    canSkipOnLastSet: Boolean,
    moveIntervalUp: (TimerInterval) -> Unit,
    moveIntervalDown: (TimerInterval) -> Unit,
    deleteInterval: (TimerInterval) -> Unit,
    duplicateInterval: (TimerInterval) -> Unit,
    updateDuration: (TimerInterval, Int) -> Unit,
    updateName: (TimerInterval, String) -> Unit,
    updateColor: (TimerInterval, Color) -> Unit,
    updateSkipOnLastSet: (TimerInterval, Boolean) -> Unit,
    updateCountUp: (TimerInterval, Boolean) -> Unit,
    updateManualNext: (TimerInterval, Boolean) -> Unit,
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

            var colorPickerVisible by remember { mutableStateOf(false) }
            Box(modifier = Modifier.size(48.dp).background(interval.color).clickable {
                colorPickerVisible = true
            })

            if (colorPickerVisible) {
                ColorPicker {
                    if (it != null) {
                        updateColor(interval, it)
                    }
                    colorPickerVisible = false
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Skip on last set")

                Spacer(modifier = Modifier.weight(1f))

                Switch(
                    enabled = canSkipOnLastSet,
                    checked = interval.skipOnLastSet,
                    onCheckedChange = { updateSkipOnLastSet(interval, it) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Count up")

                Spacer(modifier = Modifier.weight(1f))

                Switch(
                    checked = interval.countUp,
                    onCheckedChange = { updateCountUp(interval, it) }
                )
            }


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Manual next")

                Spacer(modifier = Modifier.weight(1f))

                Switch(
                    checked = interval.manualNext,
                    onCheckedChange = { updateManualNext(interval, it) }
                )
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ColorPicker(
    updateColor: (Color?) -> Unit,
) {
    ModalBottomSheet(onDismissRequest = { updateColor(null) }) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ColorPickerBox(Color.Red) {
                updateColor(it)
            }
            ColorPickerBox(Color.Green) {
                updateColor(it)
            }
            ColorPickerBox(Color.Blue) {
                updateColor(it)
            }
            ColorPickerBox(Color.Yellow) {
                updateColor(it)
            }
        }
    }
}

private fun timeFormatter(time: Int): String {
    return time.timeFormatted()
}

@Composable
private fun NumberIncrement(
    value: Int,
    formatter: (Int) -> String = { "$it" },
    onChange: (Int) -> Unit,
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

@Composable
private fun ColorPickerBox(color: Color, onClick: (Color) -> Unit) {
    Box(modifier = Modifier.size(48.dp).background(color).clickable {
        onClick(color)
    })
}