package com.timerx.ui.create

import com.timerx.database.ITimerRepository
import com.timerx.domain.Timer
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import moe.tlaster.precompose.viewmodel.ViewModel

class CreateViewModel(
    timerId: Long = -1,
    private val timerDatabase: ITimerRepository
) : ViewModel() {

    private val defaultTimerSet = TimerSet(
        repetitions = 5, intervals = persistentListOf(
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

    private var sets: MutableList<TimerSet>

    data class State(
        val timerName: String = "",
        val sets: ImmutableList<TimerSet> = persistentListOf()
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val timerEditing: Timer?

    init {
        if (timerId != -1L) {
            timerEditing = timerDatabase.getTimer(timerId)
            sets = timerEditing.sets.toMutableList()

            _state.update {
                it.copy(
                    timerName = timerEditing.name,
                    sets = sets.toPersistentList()
                )
            }
        } else {
            timerEditing = null

            sets = mutableListOf(
                TimerSet(
                    repetitions = 1, intervals = persistentListOf(
                        TimerInterval(
                            name = "Prepare", duration = 10
                        )
                    )
                ), defaultTimerSet.copy()
            )
            _state.update {
                it.copy(sets = sets.toPersistentList())
            }
        }
    }

    fun updateTimerName(name: String) {
        _state.value = state.value.copy(timerName = name)
    }

    fun save() {
        val name = state.value.timerName.ifBlank {
//            val time = Calendar.getInstance().time
//            val formatter = SimpleDateFormat.getDateTimeInstance()
//            formatter.format(time)
            "lolololol"
        }
        if (timerEditing != null) {
            timerDatabase.updateTimer(Timer(timerEditing.id, name, sets = state.value.sets))
        } else {
            timerDatabase.insertTimer(Timer(-1, name, sets = state.value.sets))
        }
    }

    fun addSet() {
        sets.add(defaultTimerSet.copy())
        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun addInterval(timerSet: TimerSet) {
        val index = sets.indexOfFirst { it === timerSet }
        sets[index] = timerSet.copy(
            intervals = (timerSet.intervals + defaultInterval.copy()).toPersistentList()
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
            TimerSet(-1, repetitions, newIntervals.toPersistentList())
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun deleteInterval(interval: TimerInterval) {
        sets = sets.map { (_, repetitions, intervals) ->
            TimerSet(-1, repetitions, intervals.filter {
                interval !== it
            }.toPersistentList())
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
            }.toPersistentList())
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
            }.toPersistentList())
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
                TimerSet(-1, set.repetitions, mutable.toPersistentList())
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
                TimerSet(-1, set.repetitions, mutable.toPersistentList())
            } else {
                set
            }
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }
}