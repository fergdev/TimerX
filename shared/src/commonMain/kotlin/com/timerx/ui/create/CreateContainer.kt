package com.timerx.ui.create

import com.timerx.beep.IBeepManager
import com.timerx.database.ITimerRepository
import com.timerx.domain.Timer
import com.timerx.vibration.IVibrationManager
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.init
import pro.respawn.flowmvi.plugins.reducePlugin
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.created_value

@OptIn(FormatStringsInDatetimeFormats::class)
private val dateTimeFormat = LocalDateTime.Format {
    byUnicodePattern("yyyy-MM-dd HH:mm:ss")
}

internal class CreateContainer(
    timerId: Long,
    private val timerDatabase: ITimerRepository,
    private val beepManager: IBeepManager,
    private val vibrationManger: IVibrationManager
) : Container<CreateScreenState, CreateScreenIntent, Nothing> {
    private val defaultGenerator = DefaultGenerator()

    private var timerEditing: Timer? = null

    override val store = store(CreateScreenState()) {
        init {
            if (timerId != -1L) {
                launch {
                    val timer = timerDatabase.getTimer(timerId).first()
                    timerEditing = timer

                    updateState {
                        CreateScreenState(
                            timerName = timer.name,
                            sets = timer.sets.toPersistentList(),
                            finishColor = timer.finishColor,
                            finishBeep = timer.finishBeep,
                            finishVibration = timer.finishVibration,
                        )
                    }
                }
            } else {
                updateState {
                    CreateScreenState(
                        sets = persistentListOf(
                            defaultGenerator.prepare(),
                            defaultGenerator.defaultTimerSet()
                        )
                    )
                }
            }
        }

        install(
            reduceIntent(
                defaultGenerator,
                timerDatabase,
                beepManager,
                vibrationManger,
                timerId
            )
        )
    }
}

private inline fun reduceIntent(
    defaultGenerator: DefaultGenerator,
    timerDatabase: ITimerRepository,
    beepManager: IBeepManager,
    vibrationManger: IVibrationManager,
    timerId: Long
) =
    reducePlugin<CreateScreenState, CreateScreenIntent, Nothing> {
        when (it) {
            is CreateScreenIntent.UpdateTimerName -> {
                updateState { this.copy(timerName = it.timerName) }
            }

            CreateScreenIntent.AddSet -> {
                updateState {
                    this.copy(
                        sets = (this.sets + defaultGenerator.defaultTimerSet()).toPersistentList()
                    )
                }
            }

            is CreateScreenIntent.DeleteSet -> {
                updateState {
                    this.copy(
                        sets = this.sets.filter { set ->
                            it.set == set
                        }.toPersistentList()
                    )
                }
            }

            is CreateScreenIntent.DuplicateSet -> {
                updateState {
                    val index = this.sets.indexOfFirst { set -> set.id == it.set.id }
                    val mutableSets = sets.toMutableList()
                    mutableSets.add(
                        index,
                        sets[index].copy(
                            id = defaultGenerator.getNextId(),
                            intervals = it.set.intervals.map { interval ->
                                interval.copy(id = defaultGenerator.getNextId())
                            }.toPersistentList()
                        )
                    )
                    this.copy(sets = mutableSets.toPersistentList())
                }
            }

            is CreateScreenIntent.UpdateSetRepetitions -> {
                updateState {
                    val index = this.sets.indexOfFirst { set -> set.id == it.set.id }
                    val mutableSets = sets.toMutableList()
                    mutableSets[index] = sets[index].copy(repetitions = it.repetitions)
                    this.copy(sets = mutableSets.toPersistentList())
                }
            }

            is CreateScreenIntent.DeleteInterval -> {
                updateState {
                    this.copy(
                        sets = this.sets.map { set ->
                            if (set.intervals.contains(it.interval)) {
                                set.copy(
                                    intervals = set.intervals.filter { interval ->
                                        it.interval.id != interval.id
                                    }.toPersistentList()
                                )
                            } else {
                                set
                            }
                        }.toPersistentList()
                    )
                }
            }

            is CreateScreenIntent.DuplicateInterval -> {
                updateState {
                    this.copy(
                        sets = this.sets.map { set ->
                            val index = set.intervals.indexOf(it.interval)
                            if (index != -1) {
                                set.copy(
                                    intervals = (set.intervals + set.intervals[index].copy()).toPersistentList()
                                )
                            } else {
                                set
                            }
                        }.toPersistentList()
                    )
                }
            }

            is CreateScreenIntent.MoveInterval -> {
                updateState {
                    this.copy(
                        sets = this.sets.map { set ->
                            if (set == it.set) {
                                val mutable = set.intervals.toMutableList()
                                val toMove = mutable.removeAt(it.from)
                                mutable.add(it.to, toMove)
                                set.copy(intervals = mutable.toPersistentList())
                            } else {
                                set
                            }
                        }.toPersistentList()
                    )
                }
            }

            is CreateScreenIntent.NewInterval -> {
                updateState {
                    val index = sets.indexOfFirst { set -> set.id == it.set.id }
                    val mutableSets = sets.toMutableList()
                    mutableSets[index] = it.set.copy(
                        intervals = (it.set.intervals + defaultGenerator.defaultInterval())
                            .toPersistentList()
                    )
                    this.copy(sets = mutableSets.toPersistentList())
                }
            }

            is CreateScreenIntent.SwapSet -> {
                updateState {
                    val mutableSets = sets.toMutableList()
                    val set = mutableSets.removeAt(it.from)
                    mutableSets.add(it.to, set)
                    this.copy(sets = mutableSets.toPersistentList())
                }

            }

            is CreateScreenIntent.UpdateFinishBeep -> {
                updateState {
                    copy(finishBeep = it.beep)
                }
                beepManager.beep(it.beep)
            }

            is CreateScreenIntent.UpdateFinishColor -> {
                updateState {
                    copy(finishColor = it.color)
                }
            }

            is CreateScreenIntent.UpdateFinishVibration -> {
                updateState {
                    copy(finishVibration = finishVibration)
                }
                vibrationManger.vibrate(it.vibration)
            }

            is CreateScreenIntent.UpdateIntervalBeep -> {
                updateState {
                    val newSets = sets.map { set ->
                        val index = set.intervals.indexOf(it.interval)
                        if (index != -1) {
                            val interval = set.intervals[index]
                            val mutableIntervals = set.intervals.toMutableList()
                            mutableIntervals[index] = interval.copy(
                                beep = it.beep
                            )
                            set.copy(intervals = mutableIntervals.toPersistentList())
                        } else {
                            set
                        }
                    }.toPersistentList()
                    copy(sets = newSets)
                }
                beepManager.beep(it.beep)
            }

            is CreateScreenIntent.UpdateIntervalColor -> {
                updateState {
                    val newSets = sets.map { set ->
                        val index = set.intervals.indexOf(it.interval)
                        if (index != -1) {
                            val interval = set.intervals[index]
                            val mutableIntervals = set.intervals.toMutableList()
                            mutableIntervals[index] = interval.copy(
                                color = it.color
                            )
                            set.copy(intervals = mutableIntervals.toPersistentList())
                        } else {
                            set
                        }
                    }.toPersistentList()
                    copy(sets = newSets)
                }
            }

            is CreateScreenIntent.UpdateIntervalCountUp -> {
                updateState {
                    val newSets = sets.map { set ->
                        val index = set.intervals.indexOf(it.interval)
                        if (index != -1) {
                            val interval = set.intervals[index]
                            val mutableIntervals = set.intervals.toMutableList()
                            mutableIntervals[index] = interval.copy(
                                countUp = it.countUp
                            )
                            set.copy(intervals = mutableIntervals.toPersistentList())
                        } else {
                            set
                        }
                    }.toPersistentList()
                    copy(sets = newSets)
                }
            }

            is CreateScreenIntent.UpdateIntervalDuration -> {
                updateState {
                    val newSets = sets.map { set ->
                        val index = set.intervals.indexOf(it.interval)
                        if (index != -1) {
                            val interval = set.intervals[index]
                            val mutableIntervals = set.intervals.toMutableList()
                            mutableIntervals[index] = interval.copy(
                                duration = it.duration
                            )
                            set.copy(intervals = mutableIntervals.toPersistentList())
                        } else {
                            set
                        }
                    }.toPersistentList()
                    copy(sets = newSets)
                }
            }

            is CreateScreenIntent.UpdateIntervalFinalCountDown -> {
                updateState {
                    val newSets = sets.map { set ->
                        val index = set.intervals.indexOf(it.interval)
                        if (index != -1) {
                            val interval = set.intervals[index]
                            val mutableIntervals = set.intervals.toMutableList()
                            mutableIntervals[index] = interval.copy(
                                finalCountDown = it.finalCountDown
                            )
                            set.copy(intervals = mutableIntervals.toPersistentList())
                        } else {
                            set
                        }
                    }.toPersistentList()
                    copy(sets = newSets)
                }
            }

            is CreateScreenIntent.UpdateIntervalManualNext -> {
                updateState {
                    val newSets = sets.map { set ->
                        val index = set.intervals.indexOf(it.interval)
                        if (index != -1) {
                            val interval = set.intervals[index]
                            val mutableIntervals = set.intervals.toMutableList()
                            mutableIntervals[index] = interval.copy(
                                manualNext = it.manualNext
                            )
                            set.copy(intervals = mutableIntervals.toPersistentList())
                        } else {
                            set
                        }
                    }.toPersistentList()
                    copy(sets = newSets)
                }
            }

            is CreateScreenIntent.UpdateIntervalName -> {
                updateState {
                    val newSets = sets.map { set ->
                        val index = set.intervals.indexOf(it.interval)
                        if (index != -1) {
                            val interval = set.intervals[index]
                            val mutableIntervals = set.intervals.toMutableList()
                            mutableIntervals[index] = interval.copy(
                                name = it.name
                            )
                            set.copy(intervals = mutableIntervals.toPersistentList())
                        } else {
                            set
                        }
                    }.toPersistentList()
                    copy(sets = newSets)
                }
            }

            is CreateScreenIntent.UpdateIntervalSkipOnLastSet -> {
                updateState {
                    val newSets = sets.map { set ->
                        val index = set.intervals.indexOf(it.interval)
                        if (index != -1) {
                            val interval = set.intervals[index]
                            val mutableIntervals = set.intervals.toMutableList()
                            mutableIntervals[index] = interval.copy(
                                skipOnLastSet = it.skipOnLastSet
                            )
                            set.copy(intervals = mutableIntervals.toPersistentList())
                        } else {
                            set
                        }
                    }.toPersistentList()
                    copy(sets = newSets)
                }
            }

            is CreateScreenIntent.UpdateIntervalVibration -> {
                updateState {
                    val newSets = sets.map { set ->
                        val index = set.intervals.indexOf(it.interval)
                        if (index != -1) {
                            val interval = set.intervals[index]
                            val mutableIntervals = set.intervals.toMutableList()
                            mutableIntervals[index] = interval.copy(
                                vibration = it.vibration
                            )
                            set.copy(intervals = mutableIntervals.toPersistentList())
                        } else {
                            set
                        }
                    }.toPersistentList()
                    copy(sets = newSets)
                }
                vibrationManger.vibrate(it.vibration)
            }

            CreateScreenIntent.Save -> {
                withState {
                    val name = runBlocking {
                        timerName.ifBlank {
                            getString(
                                Res.string.created_value,
                                Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                                    .format(dateTimeFormat)
                            )
                        }
                    }
                    launch {
                        runBlocking {
                            val newSets = sets
                                .filter { set -> set.intervals.isNotEmpty() }
                                .map { set ->
                                    set.copy(
                                        id = 0,
                                        intervals = set.intervals.map { interval ->
                                            interval.copy(
                                                id = 0
                                            )
                                        }.toPersistentList()
                                    )
                                }
                                .toPersistentList()
                            if (timerId != -1L) {
                                val timerEditing = timerDatabase.getTimer(timerId).first()
                                timerDatabase.updateTimer(
                                    Timer(
                                        id = timerEditing.id,
                                        sortOrder = timerEditing.sortOrder,
                                        name = name,
                                        sets = newSets,
                                        finishColor = finishColor,
                                        finishBeep = finishBeep,
                                        finishVibration = finishVibration,
                                        startedCount = timerEditing.startedCount,
                                        completedCount = timerEditing.completedCount,
                                        createdAt = timerEditing.createdAt
                                    )
                                )
                            } else {
                                timerDatabase.insertTimer(
                                    Timer(
                                        name = name,
                                        sets = newSets,
                                        finishColor = finishColor,
                                        finishBeep = finishBeep,
                                        finishVibration = finishVibration,
                                        createdAt = Clock.System.now()
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
