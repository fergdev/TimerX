package com.timerx.android.main

import androidx.lifecycle.ViewModel
import com.timerx.database.TimerDatabase
import com.timerx.domain.Timer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MainViewModel(private val timerRepository: TimerDatabase) : ViewModel() {

    data class State(val timers: ImmutableList<Timer> = persistentListOf())

    private val _stateFlow = MutableStateFlow(State())
    val state: StateFlow<State> = _stateFlow

    init {
        refreshData()
    }

    fun refreshData() {
        _stateFlow.update {
            State(timerRepository.getTimers().toPersistentList())
        }
    }

    fun deleteTimer(timer: Timer) {
        timerRepository.deleteTimer(timer)
        refreshData()
    }

    fun duplicateTimer(timer: Timer) {
        timerRepository.duplicate(timer)
        refreshData()
    }
}