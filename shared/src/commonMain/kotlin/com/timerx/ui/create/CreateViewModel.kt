@file:OptIn(ExperimentalResourceApi::class)

package com.timerx.ui.create

import androidx.compose.ui.graphics.Color
import com.timerx.database.ITimerRepository
import com.timerx.domain.Timer
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import moe.tlaster.precompose.viewmodel.ViewModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.getString
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.created_value
import timerx.shared.generated.resources.prepare
import timerx.shared.generated.resources.rest
import timerx.shared.generated.resources.work

class CreateViewModel(
    timerName: String = "",
    private val timerDatabase: ITimerRepository
) : ViewModel() {

    private val workString: String = runBlocking { getString(Res.string.work) }
    private val restString: String = runBlocking { getString(Res.string.rest) }

    private val defaultTimerSet = TimerSet(
        repetitions = 5,
        intervals = persistentListOf(
            TimerInterval(
                name = workString,
                duration = 30,
                color = Color.Green
            ),
            TimerInterval(
                name = restString,
                duration = 30,
                color = Color.Red
            )
        )
    )
    private val defaultInterval = TimerInterval(
        name = workString,
        duration = 30,
        color = Color.Green
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

            sets = runBlocking {
                mutableListOf(
                    TimerSet(
                        repetitions = 1, intervals = persistentListOf(
                            TimerInterval(
                                name = getString(Res.string.prepare),
                                duration = 10,
                                color = Color.Yellow
                            )
                        )
                    ), defaultTimerSet.copy()
                )
            }
            _state.update {
                it.copy(sets = sets.toPersistentList())
            }
        }
    }

    fun updateTimerName(name: String) {
        _state.value = state.value.copy(timerName = name)
    }

    fun save() {
        val name = runBlocking {
            state.value.timerName.ifBlank {
                getString(
                    Res.string.created_value,
                    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                        .format(dateTimeFormat)
                )
            }
        }
        if (timerEditing != null) {
            timerDatabase.updateTimer(
                Timer(
                    id = timerEditing.id,
                    name = name,
                    sets = state.value.sets,
                    finishColor = state.value.finishColor
                )
            )
        } else {
            timerDatabase.insertTimer(
                Timer(
                    name = name,
                    sets = state.value.sets,
                    finishColor = state.value.finishColor
                )
            )
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

    fun updateRepetitions(timerSet: TimerSet, repetitions: Int) {
        if (repetitions < 1) return
        val index = sets.indexOfFirst { it === timerSet }
        sets[index] = timerSet.copy(
            repetitions = repetitions,
            intervals = if (repetitions == 1) {
                timerSet.intervals.map {
                    it.copy(skipOnLastSet = false)
                }
            } else {
                timerSet.intervals
            }.toImmutableList()
        )
        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun updateIntervalDuration(timerInterval: TimerInterval, duration: Int) {
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

    fun updateSkipOnLastSet(timerInterval: TimerInterval, skipOnLastSet: Boolean) {
        sets = sets.map { (_, repetitions, intervals) ->
            TimerSet("", repetitions, intervals.map {
                if (it === timerInterval) {
                    it.copy(skipOnLastSet = skipOnLastSet)
                } else {
                    it
                }
            }.toPersistentList())
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun updateCountUp(timerInterval: TimerInterval, countUp: Boolean) {
        sets = sets.map { (_, repetitions, intervals) ->
            TimerSet("", repetitions, intervals.map {
                if (it === timerInterval) {
                    it.copy(countUp = countUp)
                } else {
                    it
                }
            }.toPersistentList())
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    fun updateManualNext(timerInterval: TimerInterval, manualNext: Boolean) {
        sets = sets.map { (_, repetitions, intervals) ->
            TimerSet("", repetitions, intervals.map {
                if (it === timerInterval) {
                    it.copy(manualNext = manualNext)
                } else {
                    it
                }
            }.toPersistentList())
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }
}
