package com.timerx.android.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import com.timerx.domain.formatted
import com.timerx.domain.length
import org.koin.androidx.compose.getViewModel

@Composable
fun CreateScreen(
    navController: NavHostController,
    viewModel: CreateViewModel = getViewModel()
) {
    val state by viewModel.state.collectAsState()
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { navController.navigateUp() }) {
                Text(text = "Home")
            }
            Text(text = "Create Timer")
            Button(enabled = state.timerName.isNotEmpty() && state.sets.isNotEmpty(), onClick = {
                viewModel.createTimer()
                navController.navigateUp()
            }) {
                Text(text = "Create")
            }
        }

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                    value = state.timerName,
                    label = { Text(text = "Timer name") },
                    onValueChange = { viewModel.updateTimerName(it) })
            }
            items(state.sets) {
                Set(
                    it,
                    viewModel::addInterval,
                    viewModel::deleteSet,
                    viewModel::deleteInterval,
                    viewModel::updateRepetitions,
                    viewModel::updateIntervalDuration,
                    viewModel::updateIntervalRepetitions,
                    viewModel::updateIntervalName,
                )
            }
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    Button(onClick = { viewModel.addSet() }) {
                        Text(text = "Add set")
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    Text(text = "Total ${state.sets.length().formatted()}")
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
    deleteInterval: (TimerInterval) -> Unit,
    updateRepetitions: (TimerSet, Long) -> Unit,
    updateIntervalDuration: (TimerInterval, Long) -> Unit,
    updateIntervalRepetitions: (TimerInterval, Long) -> Unit,
    updateIntervalName: (TimerInterval, String) -> Unit
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Repetitions")

            NumberIncrement(
                value = timerSet.repetitions,
                onChange = { updateRepetitions(timerSet, it) })

            timerSet.intervals.forEach { interval ->
                Interval(
                    interval,
                    deleteInterval,
                    updateIntervalDuration,
                    updateIntervalRepetitions,
                    updateIntervalName
                )
            }
            Text(text = timerSet.length().formatted())
            Button(onClick = { addInterval(timerSet) }) {
                Text(text = "Add interval")
            }
            IconButton(onClick = { deleteSet(timerSet) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}

@Composable
private fun Interval(
    interval: TimerInterval,
    deleteInterval: (TimerInterval) -> Unit,
    updateDuration: (TimerInterval, Long) -> Unit,
    updateRepetitions: (TimerInterval, Long) -> Unit,
    updateName: (TimerInterval, String) -> Unit
) {
    ElevatedCard(modifier = Modifier.padding(16.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(value = interval.name,
                label = { Text(text = "Interval name") },
                onValueChange = { updateName(interval, it) })

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Duration")
                NumberIncrement(value = interval.duration) {
                    updateDuration(interval, it)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Repetitions")
                NumberIncrement(value = interval.repetitions) {
                    updateRepetitions(interval, it)
                }
            }
            Text(text = interval.length().formatted())
            IconButton(onClick = { deleteInterval(interval) }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}

@Composable
private fun NumberIncrement(value: Long, onChange: (Long) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onChange(value - 1) }) {
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
        }
        Text(text = "$value")
        IconButton(onClick = { onChange(value + 1) }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }
}