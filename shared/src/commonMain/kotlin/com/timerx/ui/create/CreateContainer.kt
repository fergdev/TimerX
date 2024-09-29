package com.timerx.ui.create

import androidx.compose.ui.graphics.Color
import com.timerx.beep.Beep
import com.timerx.beep.IBeepManager
import com.timerx.database.ITimerRepository
import com.timerx.domain.FinalCountDown
import com.timerx.domain.Timer
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import com.timerx.ui.create.CreateScreenIntent.AddSet
import com.timerx.ui.create.CreateScreenIntent.DeleteInterval
import com.timerx.ui.create.CreateScreenIntent.DeleteSet
import com.timerx.ui.create.CreateScreenIntent.DuplicateInterval
import com.timerx.ui.create.CreateScreenIntent.DuplicateSet
import com.timerx.ui.create.CreateScreenIntent.MoveInterval
import com.timerx.ui.create.CreateScreenIntent.NewInterval
import com.timerx.ui.create.CreateScreenIntent.Save
import com.timerx.ui.create.CreateScreenIntent.SwapSet
import com.timerx.ui.create.CreateScreenIntent.UpdateFinishBeep
import com.timerx.ui.create.CreateScreenIntent.UpdateFinishColor
import com.timerx.ui.create.CreateScreenIntent.UpdateFinishVibration
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalBeep
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalColor
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalCountUp
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalDuration
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalFinalCountDown
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalManualNext
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalName
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalSkipOnLastSet
import com.timerx.ui.create.CreateScreenIntent.UpdateIntervalVibration
import com.timerx.ui.create.CreateScreenIntent.UpdateSetRepetitions
import com.timerx.ui.create.CreateScreenIntent.UpdateTimerName
import com.timerx.vibration.IVibrationManager
import com.timerx.vibration.Vibration
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.init
import pro.respawn.flowmvi.plugins.reducePlugin
import kotlin.math.max

internal class CreateContainer(
    timerId: Long,
    private val timerDatabase: ITimerRepository,
    private val beepManager: IBeepManager,
    private val vibrationManger: IVibrationManager
) : Container<CreateScreenState, CreateScreenIntent, RunScreenAction> {
    private val defaultGenerator = DefaultGenerator()
    override val store = store(CreateScreenState()) {
        init {
            launch {
                val timer = timerDatabase.getTimer(timerId).first()
                if (timer != null) {
                    updateState {
                        defaultGenerator.setMaxId(timer.sets.getMaxId())
                        CreateScreenState(
                            timerNameModel = TimerNameModel(timer.name),
                            sets = timer.sets.toPersistentList(),
                            isEditing = true,
                            finishColor = timer.finishColor,
                            finishBeep = timer.finishBeep,
                            finishVibration = timer.finishVibration,
                        )
                    }
                } else {
                    updateState {
                        CreateScreenState(
                            sets = persistentListOf(
                                defaultGenerator.prepareSet(),
                                defaultGenerator.defaultTimerSet()
                            )
                        )
                    }
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

private fun List<TimerSet>.getMaxId() =
    this.maxOf { set ->
        max(
            set.id,
            set.intervals.maxOf { interval ->
                interval.id
            }
        )
    } + 1L

private fun reduceIntent(
    defaultGenerator: DefaultGenerator,
    timerDatabase: ITimerRepository,
    beepManager: IBeepManager,
    vibrationManger: IVibrationManager,
    timerId: Long
) = reducePlugin<CreateScreenState, CreateScreenIntent, RunScreenAction> {
    when (it) {
        is UpdateTimerName -> updateState { copy(timerNameModel = TimerNameModel(name = it.timerName)) }
        AddSet ->
            updateState {
                copy(sets = (sets + defaultGenerator.defaultTimerSet()).toPersistentList())
            }

        is DeleteSet ->
            updateState {
                copy(sets = sets.filter { set -> it.set.id != set.id }.toPersistentList())
            }

        is DuplicateSet -> {
            updateState {
                val index = sets.indexOfFirst { set -> set.id == it.set.id }
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
                copy(sets = mutableSets.toPersistentList())
            }
        }

        is UpdateSetRepetitions -> {
            updateState {
                val index = sets.indexOfFirst { set -> set.id == it.set.id }
                val mutableSets = sets.toMutableList()
                mutableSets[index] = sets[index].copy(repetitions = it.repetitions)
                copy(sets = mutableSets.toPersistentList())
            }
        }

        is DeleteInterval -> {
            updateState {
                copy(
                    sets = sets.map { set ->
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

        is DuplicateInterval -> {
            updateState {
                copy(
                    sets = sets.map { set ->
                        val index = set.intervals.indexOf(it.interval)
                        if (index != -1) {
                            set.copy(
                                intervals = (
                                    set.intervals + set.intervals[index].copy(
                                        id = defaultGenerator.getNextId()
                                    )
                                    ).toPersistentList()
                            )
                        } else {
                            set
                        }
                    }.toPersistentList()
                )
            }
        }

        is MoveInterval -> {
            updateState {
                copy(
                    sets = sets.map { set ->
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

        is NewInterval -> {
            updateState {
                val index = sets.indexOfFirst { set -> set.id == it.set.id }
                val mutableSets = sets.toMutableList()
                mutableSets[index] = it.set.copy(
                    intervals = (it.set.intervals + defaultGenerator.defaultInterval())
                        .toPersistentList()
                )
                copy(sets = mutableSets.toPersistentList())
            }
        }

        is SwapSet -> {
            updateState {
                val mutableSets = sets.toMutableList()
                val set = mutableSets.removeAt(it.from)
                mutableSets.add(it.to, set)
                copy(sets = mutableSets.toPersistentList())
            }
        }

        is UpdateFinishBeep -> {
            updateState { copy(finishBeep = it.beep) }
            beepManager.beep(it.beep)
        }

        is UpdateFinishColor -> {
            updateState {
                copy(finishColor = it.color)
            }
        }

        is UpdateFinishVibration -> {
            updateState { copy(finishVibration = finishVibration) }
            vibrationManger.vibrate(it.vibration)
        }

        is UpdateIntervalBeep -> {
            updateState {
                copy(
                    sets = sets.updateInterval(
                        interval = it.interval,
                        beep = it.beep
                    )
                )
            }
            beepManager.beep(it.beep)
        }

        is UpdateIntervalColor -> updateState {
            copy(sets = sets.updateInterval(interval = it.interval, color = it.color))
        }

        is UpdateIntervalCountUp -> updateState {
            copy(sets = sets.updateInterval(interval = it.interval, countUp = it.countUp))
        }

        is UpdateIntervalDuration -> updateState {
            copy(sets = sets.updateInterval(interval = it.interval, duration = it.duration))
        }

        is UpdateIntervalFinalCountDown -> updateState {
            copy(
                sets = sets.updateInterval(
                    interval = it.interval,
                    finalCountDown = it.finalCountDown
                )
            )
        }

        is UpdateIntervalManualNext ->
            updateState {
                copy(
                    sets = sets.updateInterval(
                        interval = it.interval,
                        manualNext = it.manualNext
                    )
                )
            }

        is UpdateIntervalName ->
            updateState {
                copy(sets = sets.updateInterval(interval = it.interval, name = it.name))
            }

        is UpdateIntervalSkipOnLastSet -> updateState {
            copy(
                sets = sets.updateInterval(
                    interval = it.interval,
                    skipOnLastSet = it.skipOnLastSet
                )
            )
        }

        is UpdateIntervalVibration -> {
            updateState {
                copy(
                    sets = sets.updateInterval(
                        interval = it.interval,
                        vibration = it.vibration
                    )
                )
            }
            vibrationManger.vibrate(it.vibration)
        }

        Save -> {
            withState {
                if (timerNameModel.name.isBlank()) {
                    updateState {
                        copy(timerNameModel = timerNameModel.copy(isError = true))
                    }
                    return@withState
                }
                launch {
                    val newSets = sets.normaliseSets()
                    val timerEditing = timerDatabase.getTimer(timerId).first()
                    if (timerEditing != null) {
                        timerDatabase.updateTimer(
                            Timer(
                                id = timerEditing.id,
                                sortOrder = timerEditing.sortOrder,
                                name = timerNameModel.name,
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
                                name = timerNameModel.name,
                                sets = newSets,
                                finishColor = finishColor,
                                finishBeep = finishBeep,
                                finishVibration = finishVibration,
                                createdAt = Clock.System.now()
                            )
                        )
                    }
                    action(RunScreenAction.NavigateUp)
                }
            }
        }
    }
}

private fun PersistentList<TimerSet>.normaliseSets() =
    this.filter { set -> set.intervals.isNotEmpty() }
        .map { set ->
            set.copy(
                id = 0,
                intervals = set.intervals.map { interval ->
                    interval.copy(id = 0)
                }.toPersistentList()
            )
        }
        .toPersistentList()

private fun PersistentList<TimerSet>.updateInterval(
    interval: TimerInterval,
    name: String? = null,
    duration: Long? = null,
    color: Color? = null,
    skipOnLastSet: Boolean? = null,
    countUp: Boolean? = null,
    manualNext: Boolean? = null,
    beep: Beep? = null,
    vibration: Vibration? = null,
    finalCountDown: FinalCountDown? = null
) = this.map { set ->
    val index = set.intervals.indexOf(interval)
    if (index != -1) {
        val mutableIntervals = set.intervals.toMutableList()
        mutableIntervals[index] = interval.copy(
            name = name ?: interval.name,
            duration = duration ?: interval.duration,
            color = color ?: interval.color,
            skipOnLastSet = skipOnLastSet ?: interval.skipOnLastSet,
            countUp = countUp ?: interval.countUp,
            manualNext = manualNext ?: interval.manualNext,
            beep = beep ?: interval.beep,
            vibration = vibration ?: interval.vibration,
            finalCountDown = finalCountDown ?: interval.finalCountDown
        )
        set.copy(intervals = mutableIntervals.toPersistentList())
    } else {
        set
    }
}.toPersistentList()
