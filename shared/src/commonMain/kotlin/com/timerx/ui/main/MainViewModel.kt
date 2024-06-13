package com.timerx.ui.main

import com.timerx.database.ITimerRepository
import com.timerx.domain.Timer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import moe.tlaster.precompose.viewmodel.ViewModel

class MainViewModel(private val timerRepository: ITimerRepository) : ViewModel() {

    data class State(val timers: ImmutableList<Timer> = persistentListOf())
    class Interactions(
        val refreshData: () -> Unit,
        val deleteTimer: (Timer) -> Unit,
        val duplicateTimer: (Timer) -> Unit,
        val swapTimers: (Int, Int) -> Unit,
    )

    val interactions = Interactions(
        refreshData = ::refreshData,
        deleteTimer = ::deleteTimer,
        duplicateTimer = ::duplicateTimer,
        swapTimers = ::swapTimer,
    )

    private val _stateFlow = MutableStateFlow(State())
    val state: StateFlow<State> = _stateFlow

    init {
        refreshData()
    }

    private fun refreshData() {
        _stateFlow.update {
            State(timerRepository.getTimers().toPersistentList())
        }
    }

    private fun deleteTimer(timer: Timer) {
        timerRepository.deleteTimer(timer)
        refreshData()
    }

    private fun duplicateTimer(timer: Timer) {
        timerRepository.duplicate(timer)
        refreshData()
    }

    private fun swapTimer(from: Int, to: Int) {
        val timers = _stateFlow.value.timers.toMutableList()
        val fromTimer = timers[from]
        val toTimer = timers[to]
        timers[from] = toTimer
        timers[to] = fromTimer
        _stateFlow.update { it.copy(timers = timers.toPersistentList()) }
        timerRepository.swapTimers(from, to)
    }
}
