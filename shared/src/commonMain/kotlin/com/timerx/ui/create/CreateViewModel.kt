package com.timerx.ui.create

import androidx.compose.ui.graphics.Color
import com.timerx.beep.Beep
import com.timerx.beep.IBeepManager
import com.timerx.database.ITimerRepository
import com.timerx.domain.FinalCountDown
import com.timerx.domain.Timer
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import com.timerx.vibration.IVibrationManager
import com.timerx.vibration.Vibration
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.jetbrains.compose.resources.getString
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.created_value
import timerx.shared.generated.resources.prepare
import timerx.shared.generated.resources.rest
import timerx.shared.generated.resources.work

class CreateViewModel(
    timerId: Long,
    private val timerDatabase: ITimerRepository,
    private val beepManager: IBeepManager,
    private val vibrationManger: IVibrationManager
) : ViewModel() {

    private val workString: String = runBlocking { getString(Res.string.work) }
    private val restString: String = runBlocking { getString(Res.string.rest) }

    private var defaultIdGenerator = 0L
    private fun defaultTimerSet(): TimerSet {
        return TimerSet(
            id = defaultIdGenerator++,
            repetitions = 5,
            intervals = persistentListOf(
                TimerInterval(
                    id = defaultIdGenerator++,
                    name = workString,
                    duration = 30,
                    color = Color.Green,
                    vibration = Vibration.Medium,
                    beep = Beep.Alert,
                    finalCountDown = FinalCountDown(
                        duration = 3,
                        beep = Beep.Alert,
                        vibration = Vibration.Medium
                    )
                ),
                TimerInterval(
                    id = defaultIdGenerator++,
                    name = restString,
                    duration = 30,
                    vibration = Vibration.Medium,
                    color = Color.Blue,
                    beep = Beep.Alert2,
                    finalCountDown = FinalCountDown(
                        duration = 3,
                        beep = Beep.Alert,
                        vibration = Vibration.Medium
                    )
                )
            )
        )
    }

    private fun defaultInterval(): TimerInterval {
        return TimerInterval(
            id = defaultIdGenerator++,
            name = workString,
            duration = 30,
            color = Color.Green,
            vibration = Vibration.Medium,
            beep = Beep.Alert,
            finalCountDown = FinalCountDown(
                duration = 3,
                beep = Beep.Alert,
                vibration = Vibration.Medium
            )
        )
    }

    @OptIn(FormatStringsInDatetimeFormats::class)
    private val dateTimeFormat = LocalDateTime.Format {
        byUnicodePattern("yyyy-MM-dd HH:mm:ss")
    }

    private var sets: MutableList<TimerSet> = mutableListOf()

    class SetInteractions(
        val duplicate: (TimerSet) -> Unit,
        val delete: (TimerSet) -> Unit,
        val update: UpdateSetInteractions
    )

    class UpdateSetInteractions(
        val newInterval: (TimerSet) -> Unit,
        val moveInterval: (TimerSet, Int, Int) -> Unit,
        val updateRepetitions: (TimerSet, Int) -> Unit,
    )

    class IntervalInteractions(
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
        val updateBeep: (TimerInterval, Beep) -> Unit,
        val updateFinalCountDown: (TimerInterval, FinalCountDown) -> Unit,
        val updateVibration: (TimerInterval, Vibration) -> Unit
    )

    class Interactions(
        val updateTimerName: (String) -> Unit,
        val addSet: () -> Unit,
        val swapSet: (Int, Int) -> Unit,
        val updateFinishColor: (Color) -> Unit,
        val updateFinishAlert: (Beep) -> Unit,
        val updateFinishVibration: (Vibration) -> Unit,
        val save: () -> Unit,

        val set: SetInteractions,
        val interval: IntervalInteractions
    )

    val interactions = Interactions(
        updateTimerName = ::updateTimerName,
        addSet = ::addSet,
        swapSet = ::swapSet,
        updateFinishColor = ::onFinishColor,
        updateFinishAlert = ::updateFinishBeep,
        updateFinishVibration = ::updateFinishVibration,
        save = ::save,

        set = SetInteractions(
            duplicate = ::duplicateSet,
            delete = ::deleteSet,
            update = UpdateSetInteractions(
                newInterval = ::newInterval,
                updateRepetitions = ::updateRepetitions,
                moveInterval = ::moveInterval,
            )
        ),

        interval = IntervalInteractions(
            delete = ::deleteInterval,
            duplicate = ::duplicateInterval,
            update = UpdateIntervalInteractions(
                updateDuration = ::updateIntervalDuration,
                updateName = ::updateIntervalName,
                updateColor = ::updateIntervalColor,
                updateSkipOnLastSet = ::updateSkipOnLastSet,
                updateCountUp = ::updateCountUp,
                updateManualNext = ::updateManualNext,
                updateBeep = ::updateBeep,
                updateFinalCountDown = ::updateFinalCountDown,
                updateVibration = ::updateVibration
            ),
        )
    )

    data class State(
        val timerName: String = "",
        val finishColor: Color = Color.Red,
        val finishBeep: Beep = Beep.End,
        val finishVibration: Vibration = Vibration.Heavy,
        val sets: ImmutableList<TimerSet> = persistentListOf()
    )

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state

    private var timerEditing: Timer? = null

    init {
        if (timerId != -1L) {
            viewModelScope.launch {
                val timer = timerDatabase.getTimer(timerId).first()
                timerEditing = timer
                sets = timer.sets.toMutableList()

                _state.update {
                    runBlocking {
                        it.copy(
                            timerName = timer.name,
                            sets = sets.toPersistentList(),
                            finishColor = timer.finishColor,
                            finishBeep = timer.finishBeep,
                            finishVibration = timer.finishVibration,
                        )
                    }
                }
            }
        } else {
            timerEditing = null
            sets = runBlocking {
                mutableListOf(
                    TimerSet(
                        id = defaultIdGenerator++,
                        repetitions = 1,
                        intervals = persistentListOf(
                            TimerInterval(
                                name = getString(Res.string.prepare),
                                duration = 10,
                                color = Color.Yellow,
                                skipOnLastSet = false,
                                countUp = false,
                                manualNext = false,
                                beep = Beep.Alert,
                                vibration = Vibration.Medium,
                                finalCountDown = FinalCountDown(
                                    duration = 3,
                                    beep = Beep.Alert,
                                    vibration = Vibration.Medium
                                )
                            )
                        )
                    ),
                    defaultTimerSet()
                )
            }
            _state.update { runBlocking { it.copy(sets = sets.toPersistentList()) } }
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
        viewModelScope.launch {
            runBlocking {
                val timerEditing = this@CreateViewModel.timerEditing
                if (timerEditing != null) {
                    timerDatabase.updateTimer(
                        Timer(
                            id = timerEditing.id,
                            sortOrder = timerEditing.sortOrder,
                            name = name,
                            sets = state.value.sets
                                .filter { it.intervals.isNotEmpty() }.map {
                                    it.copy(
                                        id = 0,
                                        intervals = it.intervals.map { it.copy(id = 0) }
                                            .toPersistentList()
                                    )
                                }.toPersistentList(),
                            finishColor = state.value.finishColor,
                            finishBeep = state.value.finishBeep,
                            finishVibration = Vibration.Heavy,
                            startedCount = timerEditing.startedCount,
                            completedCount = timerEditing.completedCount,
                            createdAt = timerEditing.createdAt
                        )
                    )
                } else {
                    timerDatabase.insertTimer(
                        Timer(
                            name = name,
                            sets = state.value.sets.filter {
                                it.intervals.isNotEmpty()
                            }.map {
                                it.copy(
                                    id = 0,
                                    intervals = it.intervals.map { it.copy(id = 0) }
                                        .toPersistentList()
                                )
                            }.toPersistentList(),
                            finishColor = state.value.finishColor,
                            finishBeep = state.value.finishBeep,
                            finishVibration = state.value.finishVibration,
                            createdAt = Clock.System.now()
                        )
                    )
                }
            }
        }
    }

    private fun addSet() {
        sets.add(defaultTimerSet())
        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun newInterval(timerSet: TimerSet) {
        val index = sets.indexOfFirst { it.id == timerSet.id }
        sets[index] = timerSet.copy(
            intervals = (timerSet.intervals + defaultInterval()).toPersistentList()
        )
        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun swapSet(from: Int, to: Int) {
        val set = sets.removeAt(from)
        sets.add(to, set)

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun duplicateSet(timerSet: TimerSet) {
        val index = sets.indexOfFirst { it.id == timerSet.id }
        sets.add(
            index,
            sets[index].copy(
                id = defaultIdGenerator++,
                intervals = timerSet.intervals.map {
                    it.copy(id = defaultIdGenerator++)
                }.toPersistentList()
            )
        )
        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun deleteSet(timerSet: TimerSet) {
        val index = sets.indexOfFirst { it.id == timerSet.id }
        sets.removeAt(index)
        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun updateRepetitions(timerSet: TimerSet, repetitions: Int) {
        if (repetitions < 1) return
        val index = sets.indexOfFirst { it.id == timerSet.id }
        sets[index] =
            timerSet.copy(
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

    private fun duplicateInterval(interval: TimerInterval) {
        sets = sets.map { set ->
            val newIntervals: List<TimerInterval> = if (set.intervals.contains(interval)) {
                val index = set.intervals.indexOf(interval)
                val mutableIntervals = set.intervals.toMutableList()
                mutableIntervals.add(
                    index,
                    set.intervals[index].copy(id = defaultIdGenerator++)
                )
                mutableIntervals
            } else {
                set.intervals
            }
            set.copy(intervals = newIntervals.toPersistentList())
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun deleteInterval(interval: TimerInterval) {
        sets = sets.map { set ->
            set.copy(intervals = set.intervals.filter { interval.id != it.id }.toPersistentList())
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun updateIntervalDuration(timerInterval: TimerInterval, duration: Int) {
        if (duration < 1) return
        sets = sets.map { set ->
            set.copy(
                intervals = set.intervals.map {
                    if (it.id == timerInterval.id) {
                        it.copy(duration = duration)
                    } else {
                        it
                    }
                }.toPersistentList()
            )
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun updateIntervalName(timerInterval: TimerInterval, name: String) {
        sets = sets.map { set ->
            set.copy(
                intervals = set.intervals.map {
                    if (it.id == timerInterval.id) {
                        it.copy(name = name)
                    } else {
                        it
                    }
                }.toPersistentList()
            )
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun moveInterval(timerSet: TimerSet, from: Int, to: Int) {
        sets = sets.map { set ->
            if (timerSet == set) {
                val mutable = set.intervals.toMutableList()
                val toMove = mutable.removeAt(from)
                mutable.add(to, toMove)
                set.copy(intervals = mutable.toPersistentList())
            } else {
                set
            }
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun updateIntervalColor(timerInterval: TimerInterval, color: Color) {
        sets = sets.map { set ->
            set.copy(
                intervals = set.intervals.map {
                    if (it.id == timerInterval.id) {
                        it.copy(color = color)
                    } else {
                        it
                    }
                }.toPersistentList()
            )
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun onFinishColor(color: Color) {
        _state.value = state.value.copy(finishColor = color)
    }

    private fun updateSkipOnLastSet(timerInterval: TimerInterval, skipOnLastSet: Boolean) {
        sets = sets.map { set ->
            set.copy(
                intervals = set.intervals.map {
                    if (it.id == timerInterval.id) {
                        it.copy(skipOnLastSet = skipOnLastSet)
                    } else {
                        it
                    }
                }.toPersistentList()
            )
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun updateCountUp(timerInterval: TimerInterval, countUp: Boolean) {
        sets = sets.map { set ->
            set.copy(
                intervals = set.intervals.map {
                    if (it.id == timerInterval.id) {
                        it.copy(countUp = countUp)
                    } else {
                        it
                    }
                }.toPersistentList()
            )
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun updateManualNext(timerInterval: TimerInterval, manualNext: Boolean) {
        sets = sets.map { set ->
            set.copy(
                intervals = set.intervals.map {
                    if (it.id == timerInterval.id) {
                        it.copy(manualNext = manualNext)
                    } else {
                        it
                    }
                }.toPersistentList()
            )
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun updateBeep(timerInterval: TimerInterval, beep: Beep) {
        sets = sets.map { set ->
            set.copy(
                intervals = set.intervals.map {
                    if (it.id == timerInterval.id) {
                        it.copy(beep = beep)
                    } else {
                        it
                    }
                }.toPersistentList()
            )
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
        viewModelScope.launch {
            beepManager.beep(beep)
        }
    }

    private fun updateFinishBeep(beep: Beep) {
        _state.update { it.copy(finishBeep = beep) }
        viewModelScope.launch {
            beepManager.beep(beep)
        }
    }

    private fun updateFinishVibration(vibration: Vibration) {
        _state.update { it.copy(finishVibration = vibration) }
        viewModelScope.launch {
            vibrationManger.vibrate(vibration)
        }
    }

    private fun updateFinalCountDown(
        timerInterval: TimerInterval,
        finalCountDown: FinalCountDown
    ) {
        sets = sets.map { set ->
            set.copy(
                intervals = set.intervals.map {
                    if (it.id == timerInterval.id) {
                        it.copy(finalCountDown = finalCountDown)
                    } else {
                        it
                    }
                }.toPersistentList()
            )
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
    }

    private fun updateVibration(timerInterval: TimerInterval, vibration: Vibration) {
        sets = sets.map { set ->
            set.copy(
                intervals = set.intervals.map {
                    if (it.id == timerInterval.id) {
                        it.copy(vibration = vibration)
                    } else {
                        it
                    }
                }.toPersistentList()
            )
        }.toMutableList()

        _state.value = state.value.copy(sets = sets.toPersistentList())
        viewModelScope.launch {
            vibrationManger.vibrate(vibration)
        }
    }
}
