package com.timerx.ui.create

import androidx.compose.ui.graphics.Color
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
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import moe.tlaster.precompose.viewmodel.ViewModel

class CreateViewModel(
    timerName: String = "",
    private val timerDatabase: ITimerRepository
) : ViewModel() {

    private val defaultTimerSet = TimerSet(
        repetitions = 5, intervals = persistentListOf(
            TimerInterval(
                name = "Work", duration = 30, color = Color.Green
            ), TimerInterval(
                name = "Rest", duration = 30, color = Color.Red
            )
        )
    )
    private val defaultInterval = TimerInterval(
        name = "Work", duration = 30, color = Color.Green
    )

    @OptIn(FormatStringsInDatetimeFormats::class)
    val dateTimeFormat = LocalDateTime.Format { byUnicodePattern("yyyy-MM-dd HH:mm:ss") }

    private var sets: MutableList<TimerSet>

    data class State(
        val timerName: String = "",
        val finishColor: Color = Color.Red,
        val sets: ImmutableList<TimerSet> = persistentListOf()
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private val timerEditing: Timer?

    init {
        if (timerName != "") {
            timerEditing = timerDatabase.getTimer(timerName)
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
                            name = "Prepare", duration = 10, color = Color.Yellow
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
            "Created " + Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                .format(dateTimeFormat)
        }
        if (timerEditing != null) {
            timerDatabase.updateTimer(
                Timer(
                    id = timerEditing.id,
                    name = name,
                    sets = state.value.sets,
                    finishColor = state.value.finishColor
                ))
        } else {
            timerDatabase.insertTimer(
                Timer(
                    id = "",
                    name = name,
                    sets = state.value.sets,
                    finishColor = state.value.finishColor
                ))
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
            TimerSet("", repetitions, newIntervals.toPersistentList())
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun deleteInterval(interval: TimerInterval) {
        sets = sets.map { (_, repetitions, intervals) ->
            TimerSet("", repetitions, intervals.filter {
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
            TimerSet("", repetitions, intervals.map {
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
            TimerSet("", repetitions, intervals.map {
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
                TimerSet("", set.repetitions, mutable.toPersistentList())
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
                TimerSet("", set.repetitions, mutable.toPersistentList())
            } else {
                set
            }
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun updateIntervalColor(timerInterval: TimerInterval, color: Color) {
        sets = sets.map { (_, repetitions, intervals) ->
            TimerSet("", repetitions, intervals.map {
                if (it === timerInterval) {
                    it.copy(color = color)
                } else {
                    it
                }
            }.toPersistentList())
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun onFinishColor(color: Color) {
        _state.value = state.value.copy(finishColor = color)
    }
}