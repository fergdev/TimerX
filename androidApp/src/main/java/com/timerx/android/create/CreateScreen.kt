package com.timerx.android.create

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.timerx.database.TimerDatabase
import com.timerx.domain.Timer
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.getViewModel

class CreateViewModel(
    private val timerDatabase: TimerDatabase
) : ViewModel() {

    private val defaultTimerSet = TimerSet(
        repetitions = 5, intervals = listOf(
            TimerInterval(
                name = "Work", duration = 30
            ), TimerInterval(
                name = "Rest", duration = 30
            )
        )
    )
    private val defaultInterval = TimerInterval(
        name = "Work", duration = 30
    )
    private var sets = mutableListOf(
        TimerSet(
            repetitions = 1,
            intervals = listOf(
                TimerInterval(
                    name = "Prepare",
                    duration = 10
                )
            )
        ), defaultTimerSet.copy()
    )

    data class State(
        val timerName: String = "",
        val sets: PersistentList<TimerSet>
    )

    private val _state = MutableStateFlow(
        State(sets = sets.toPersistentList())
    )
    val state: StateFlow<State> = _state

    fun updateTimerName(name: String) {
        _state.value = state.value.copy(timerName = name)
    }

    fun createTimer() {
        timerDatabase.insertTimer(
            Timer(-1, state.value.timerName, sets = state.value.sets)
        )
    }

    fun addSet() {
        sets.add(defaultTimerSet.copy())
        _state.value = state.value.copy(
            sets = sets.toPersistentList()
        )
    }

    fun addInterval(timerSet: TimerSet) {
        val index = sets.indexOfFirst { it === timerSet }
        sets[index] = timerSet.copy(
            intervals = timerSet.intervals + defaultInterval
        )
        _state.value = state.value.copy(
            sets = sets.toPersistentList()
        )
    }

    fun deleteSet(timerSet: TimerSet) {
        val index = sets.indexOfFirst { it === timerSet }
        sets.removeAt(index)
        _state.value = state.value.copy(
            sets = sets.toPersistentList()
        )
    }

    fun deleteInterval(interval: TimerInterval) {
        sets = sets.map { (_, repetitions, intervals) ->
            TimerSet(-1, repetitions, intervals.filter {
                interval !== it
            })
        }.toMutableList()

        _state.value = state.value.copy(
            sets = sets.toPersistentList()
        )
    }

    fun updateRepetitions(timerSet: TimerSet, repetitions: Long) {
        val index = sets.indexOfFirst { it === timerSet }
        sets[index] = timerSet.copy(
            repetitions = repetitions
        )
        _state.value = state.value.copy(
            sets = sets.toPersistentList()
        )
    }

    fun updateIntervalDuration(timerInterval: TimerInterval, duration: Long) {
        sets = sets.map { (_, repetitions, intervals) ->
            TimerSet(-1, repetitions, intervals.map {
                if (it === timerInterval) {
                    it.copy(duration = duration)
                } else {
                    it
                }
            })
        }.toMutableList()

        _state.value = state.value.copy(
            sets = sets.toPersistentList()
        )
    }

    fun updateIntervalName(timerInterval: TimerInterval, name: String) {
        sets = sets.map { (_, repetitions, intervals) ->
            TimerSet(-1, repetitions, intervals.map {
                if (it === timerInterval) {
                    it.copy(name = name)
                } else {
                    it
                }
            })
        }.toMutableList()

        _state.value = state.value.copy(
            sets = sets.toPersistentList()
        )
    }
}

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
            Button(enabled = state.timerName.isNotEmpty() && state.sets.isNotEmpty(),
                onClick = {
                    viewModel.createTimer()
                    navController.navigateUp()
                }) {
                Text(text = "Create")
            }
        }

        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                TextField(modifier = Modifier.fillMaxWidth(),
                    value = state.timerName,
                    placeholder = { Text(text = "timer name") },
                    onValueChange = {
                        viewModel.updateTimerName(it)
                    })
            }
            items(state.sets) {
                Set(
                    it,
                    viewModel::addInterval,
                    viewModel::deleteSet,
                    viewModel::deleteInterval,
                    viewModel::updateRepetitions,
                    viewModel::updateIntervalDuration,
                    viewModel::updateIntervalName,
                )
            }
            item {
                Button(onClick = { viewModel.addSet() }) {
                    Text(text = "Add set")
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
    updateIntervalName: (TimerInterval, String) -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Repetitions")

            NumberIncrement(
                value = timerSet.repetitions,
                onChange = { updateRepetitions(timerSet, it) })

            timerSet.intervals.forEach { interval ->
                Interval(interval, deleteInterval, updateIntervalDuration, updateIntervalName)
            }

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
    updateName: (TimerInterval, String) -> Unit
) {
    ElevatedCard(modifier = Modifier.padding(16.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(value = interval.name, onValueChange = { updateName(interval, it) })

            NumberIncrement(value = interval.duration) {
                updateDuration(interval, it)
            }
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