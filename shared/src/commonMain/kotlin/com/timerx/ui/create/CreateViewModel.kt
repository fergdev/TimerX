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
import timerx.shared.generated.resources.create_timer
import timerx.shared.generated.resources.created_value
import timerx.shared.generated.resources.edit_timer
import timerx.shared.generated.resources.prepare
import timerx.shared.generated.resources.rest
import timerx.shared.generated.resources.work

class CreateViewModel(
    timerName: String = "", private val timerDatabase: ITimerRepository
) : ViewModel() {

    private val workString: String = runBlocking { getString(Res.string.work) }
    private val restString: String = runBlocking { getString(Res.string.rest) }

    private val defaultTimerSet = TimerSet(
        repetitions = 5, intervals = persistentListOf(
            TimerInterval(
                name = workString, duration = 30, color = Color.Green
            ), TimerInterval(
                name = restString, duration = 30, color = Color.Red
            )
        )
    )
    private val defaultInterval = TimerInterval(
        name = workString, duration = 30, color = Color.Green
    )

    @OptIn(FormatStringsInDatetimeFormats::class)
    private val dateTimeFormat = LocalDateTime.Format { byUnicodePattern("yyyy-MM-dd HH:mm:ss") }

    private var sets: MutableList<TimerSet>

    class SetInteractions(
        val moveUp: (TimerSet) -> Unit,
        val moveDown: (TimerSet) -> Unit,
        val duplicate: (TimerSet) -> Unit,
        val delete: (TimerSet) -> Unit,

        val update: UpdateSetInteractions
    )

    class UpdateSetInteractions(
        val newInterval: (TimerSet) -> Unit,
        val updateRepetitions: (TimerSet, Int) -> Unit,
    )

    class IntervalInteractions(
        val moveUp: (TimerInterval) -> Unit,
        val moveDown: (TimerInterval) -> Unit,
        val delete: (TimerInterval) -> Unit,
        val duplicate: (TimerInterval) -> Unit,

        val update: UpdateIntervalInteractions
    )

    class UpdateIntervalInteractions(
        val updateDuration: (TimerInterval, Int) -> Unit,
        val updateName: (TimerInterval, String) -> Unit,
        val updateColor: (TimerInterval, Color) -> Unit,
        val updateSkipOnLastSet: (TimerInterval, Boolean) -> Unit,
        val updateCountUp: (TimerInterval, Boolean) -> Unit,
        val updateManualNext: (TimerInterval, Boolean) -> Unit,
    )

    class Interactions(
        val updateTimerName: (String) -> Unit,
        val addSet: () -> Unit,
        val updateFinishColor: (Color) -> Unit,
        val save: () -> Unit,

        val set: SetInteractions,
        val interval: IntervalInteractions
    )

    val interactions = Interactions(
        updateTimerName = ::updateTimerName,
        addSet = ::addSet,
        updateFinishColor = ::onFinishColor,
        save = ::save,

        set = SetInteractions(
            moveUp = ::moveSetUp,
            moveDown = ::moveSetDown,
            duplicate = ::duplicateSet,
            delete = ::deleteSet,
            update = UpdateSetInteractions(
                newInterval = ::newInterval,
                updateRepetitions = ::updateRepetitions
            )
        ),

        interval = IntervalInteractions(
            delete = ::deleteInterval,
            duplicate = ::duplicateInterval,
            moveUp = ::moveIntervalUp,
            moveDown = ::moveIntervalDown,
            update = UpdateIntervalInteractions(
                updateDuration = ::updateIntervalDuration,
                updateName = ::updateIntervalName,
                updateColor = ::updateIntervalColor,
                updateSkipOnLastSet = ::updateSkipOnLastSet,
                updateCountUp = ::updateCountUp,
                updateManualNext = ::updateManualNext
            ),
        )
    )

    data class State(
        val screenTitle: String = "",
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
                runBlocking {
                    it.copy(
                        screenTitle = getString(Res.string.edit_timer),
                        timerName = timerEditing.name,
                        sets = sets.toPersistentList()
                    )
                }
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
                runBlocking {
                    it.copy(
                        screenTitle = getString(Res.string.create_timer),
                        sets = sets.toPersistentList()
                    )
                }
            }
        }
    }

    private fun updateTimerName(name: String) {
        _state.value = state.value.copy(timerName = name)
    }

    private fun save() {
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
                    name = name, sets = state.value.sets, finishColor = state.value.finishColor
                )
            )
        }
    }

    private fun addSet() {
        sets.add(defaultTimerSet.copy())
        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun newInterval(timerSet: TimerSet) {
        val index = sets.indexOfFirst { it === timerSet }
        sets[index] = timerSet.copy(
            intervals = (timerSet.intervals + defaultInterval.copy()).toPersistentList()
        )
        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun moveSetUp(timerSet: TimerSet) {
        val index = sets.indexOfFirst { it === timerSet }
        if (index == 0) return

        sets.removeAt(index)
        sets.add(index - 1, timerSet)

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun moveSetDown(timerSet: TimerSet) {
        val index = sets.indexOfFirst { it === timerSet }
        if (index == sets.size - 1) return

        sets.removeAt(index)
        sets.add(index + 1, timerSet)

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun duplicateSet(timerSet: TimerSet) {
        val index = sets.indexOfFirst { it === timerSet }
        sets.add(index, sets[index].copy())
        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun deleteSet(timerSet: TimerSet) {
        val index = sets.indexOfFirst { it === timerSet }
        sets.removeAt(index)
        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun updateRepetitions(timerSet: TimerSet, repetitions: Int) {
        if (repetitions < 1) return
        val index = sets.indexOfFirst { it === timerSet }
        sets[index] =
            timerSet.copy(repetitions = repetitions, intervals = if (repetitions == 1) {
                timerSet.intervals.map {
                    it.copy(skipOnLastSet = false)
                }
            } else {
                timerSet.intervals
            }.toImmutableList())
        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun duplicateInterval(interval: TimerInterval) {
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

    private fun deleteInterval(interval: TimerInterval) {
        sets = sets.map { (_, repetitions, intervals) ->
            TimerSet("", repetitions, intervals.filter {
                interval !== it
            }.toPersistentList())
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }


    private fun updateIntervalDuration(timerInterval: TimerInterval, duration: Int) {
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

    private fun updateIntervalName(timerInterval: TimerInterval, name: String) {
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

    private fun moveIntervalUp(timerInterval: TimerInterval) {
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

    private fun moveIntervalDown(timerInterval: TimerInterval) {
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

    private fun updateIntervalColor(timerInterval: TimerInterval, color: Color) {
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

    private fun onFinishColor(color: Color) {
        _state.value = state.value.copy(finishColor = color)
    }

    private fun updateSkipOnLastSet(timerInterval: TimerInterval, skipOnLastSet: Boolean) {
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

    private fun updateCountUp(timerInterval: TimerInterval, countUp: Boolean) {
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

    private fun updateManualNext(timerInterval: TimerInterval, manualNext: Boolean) {
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
