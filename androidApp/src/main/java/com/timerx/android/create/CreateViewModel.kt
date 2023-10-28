package com.timerx.android.create

import androidx.lifecycle.ViewModel
import com.timerx.database.TimerDatabase
import com.timerx.domain.Timer
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Calendar

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
        val name = state.value.timerName.ifBlank {
            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat.getDateTimeInstance()
            formatter.format(time)
        }
        timerDatabase.insertTimer(Timer(-1, name, sets = state.value.sets))
    }

    fun addSet() {
        sets.add(defaultTimerSet.copy())
        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun addInterval(timerSet: TimerSet) {
        val index = sets.indexOfFirst { it === timerSet }
        sets[index] = timerSet.copy(
            intervals = timerSet.intervals + defaultInterval.copy()
        )
        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun moveSetUp(timerSet: TimerSet) {
        val index = sets.indexOfFirst { it === timerSet }
        if (index == 0) return

        sets.removeAt(index)
        sets.add(index - 1, timerSet)

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun moveSetDown(timerSet: TimerSet) {
        val index = sets.indexOfFirst { it === timerSet }
        if (index == sets.size - 1) return

        sets.removeAt(index)
        sets.add(index + 1, timerSet)

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun duplicateSet(timerSet: TimerSet) {
        val index = sets.indexOfFirst { it === timerSet }
        sets.add(index, sets[index].copy())
        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun deleteSet(timerSet: TimerSet) {
        val index = sets.indexOfFirst { it === timerSet }
        sets.removeAt(index)
        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun duplicateInterval(interval: TimerInterval) {
        sets = sets.map { (_, repetitions, intervals) ->
            val newIntervals: List<TimerInterval> = if (intervals.contains(interval)) {
                val index = intervals.indexOf(interval)
                val mutableIntervals = intervals.toMutableList()
                mutableIntervals.add(index, intervals[index].copy())
                mutableIntervals
            } else {
                intervals
            }
            TimerSet(-1, repetitions, newIntervals)
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun deleteInterval(interval: TimerInterval) {
        sets = sets.map { (_, repetitions, intervals) ->
            TimerSet(-1, repetitions, intervals.filter {
                interval !== it
            })
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun updateRepetitions(timerSet: TimerSet, repetitions: Long) {
        if (repetitions < 1) return
        val index = sets.indexOfFirst { it === timerSet }
        sets[index] = timerSet.copy(
            repetitions = repetitions
        )
        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun updateIntervalDuration(timerInterval: TimerInterval, duration: Long) {
        if (duration < 1) return
        sets = sets.map { (_, repetitions, intervals) ->
            TimerSet(-1, repetitions, intervals.map {
                if (it === timerInterval) {
                    it.copy(duration = duration)
                } else {
                    it
                }
            })
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
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

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun moveIntervalUp(timerInterval: TimerInterval) {
        sets = sets.map { set ->
            val index = set.intervals.indexOf(timerInterval)
            if (index > 0) {
                val mutable = set.intervals.toMutableList()
                mutable.removeAt(index)
                mutable.add(index - 1, timerInterval)
                TimerSet(-1, set.repetitions, mutable.toList())
            } else {
                set
            }
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun moveIntervalDown(timerInterval: TimerInterval) {
        sets = sets.map { set ->
            val index = set.intervals.indexOf(timerInterval)
            if (index != -1 && index != set.intervals.size - 1) {
                val mutable = set.intervals.toMutableList()
                mutable.removeAt(index)
                mutable.add(index + 1, timerInterval)
                TimerSet(-1, set.repetitions, mutable.toList())
            } else {
                set
            }
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }
}