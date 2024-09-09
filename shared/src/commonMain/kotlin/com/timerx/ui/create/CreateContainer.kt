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
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
) : Container<CreateScreenState, CreateScreenIntent, RunScreenAction> {
    private val defaultGenerator = DefaultGenerator()
    override val store = store(CreateScreenState()) {
        init {
            if (timerId != -1L) {
                launch {
                    timerDatabase.getTimer(timerId).first().let {
                        updateState {
                            CreateScreenState(
                                timerName = it.name,
                                sets = it.sets,
                                finishColor = it.finishColor,
                                finishBeep = it.finishBeep,
                                finishVibration = it.finishVibration,
                            )
                        }
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
) = reducePlugin<CreateScreenState, CreateScreenIntent, RunScreenAction> {
    when (it) {
        is CreateScreenIntent.UpdateTimerName -> updateState { copy(timerName = it.timerName) }
        CreateScreenIntent.AddSet ->
            updateState {
                copy(sets = (sets + defaultGenerator.defaultTimerSet()).toPersistentList())
            }

        is CreateScreenIntent.DeleteSet ->
            updateState {
                copy(sets = sets.filter { set -> it.set == set }.toPersistentList())
            }

        is CreateScreenIntent.DuplicateSet -> {
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

        is CreateScreenIntent.UpdateSetRepetitions -> {
            updateState {
                val index = sets.indexOfFirst { set -> set.id == it.set.id }
                val mutableSets = sets.toMutableList()
                mutableSets[index] = sets[index].copy(repetitions = it.repetitions)
                copy(sets = mutableSets.toPersistentList())
            }
        }

        is CreateScreenIntent.DeleteInterval -> {
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

        is CreateScreenIntent.DuplicateInterval -> {
            updateState {
                copy(
                    sets = sets.map { set ->
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

        is CreateScreenIntent.NewInterval -> {
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

        is CreateScreenIntent.SwapSet -> {
            updateState {
                val mutableSets = sets.toMutableList()
                val set = mutableSets.removeAt(it.from)
                mutableSets.add(it.to, set)
                copy(sets = mutableSets.toPersistentList())
            }

        }

        is CreateScreenIntent.UpdateFinishBeep -> {
            updateState { copy(finishBeep = it.beep) }
            beepManager.beep(it.beep)
        }

        is CreateScreenIntent.UpdateFinishColor -> {
            updateState {
                copy(finishColor = it.color)
            }
        }

        is CreateScreenIntent.UpdateFinishVibration -> {
            updateState { copy(finishVibration = finishVibration) }
            vibrationManger.vibrate(it.vibration)
        }

        is CreateScreenIntent.UpdateIntervalBeep -> {
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

        is CreateScreenIntent.UpdateIntervalColor -> updateState {
            copy(sets = sets.updateInterval(interval = it.interval, color = it.color))
        }

        is CreateScreenIntent.UpdateIntervalCountUp -> updateState {
            copy(sets = sets.updateInterval(interval = it.interval, countUp = it.countUp))
        }

        is CreateScreenIntent.UpdateIntervalDuration -> updateState {
            copy(sets = sets.updateInterval(interval = it.interval, duration = it.duration))
        }

        is CreateScreenIntent.UpdateIntervalFinalCountDown -> updateState {
            copy(
                sets = sets.updateInterval(
                    interval = it.interval,
                    finalCountDown = it.finalCountDown
                )
            )
        }

        is CreateScreenIntent.UpdateIntervalManualNext ->
            updateState {
                copy(
                    sets = sets.updateInterval(
                        interval = it.interval,
                        manualNext = it.manualNext
                    )
                )
            }

        is CreateScreenIntent.UpdateIntervalName ->
            updateState {
                copy(sets = sets.updateInterval(interval = it.interval, name = it.name))
            }

        is CreateScreenIntent.UpdateIntervalSkipOnLastSet -> updateState {
            copy(
                sets = sets.updateInterval(
                    interval = it.interval,
                    skipOnLastSet = it.skipOnLastSet
                )
            )
        }

        is CreateScreenIntent.UpdateIntervalVibration -> {
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

        CreateScreenIntent.Save -> {
            withState {
                val name = timerName.ifBlank {
                    getString(
                        Res.string.created_value,
                        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                            .format(dateTimeFormat)
                    )
                }
                launch {
                    val newSets = sets.normaliseSets()
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
    duration: Int? = null,
    color: Color? = null,
    skipOnLastSet: Boolean? = null,
    countUp: Boolean? = null,
    manualNext: Boolean? = null,
    beep: Beep? = null,
    vibration: Vibration? = null,
    finalCountDown: FinalCountDown? = null
) =
    this.map { set ->
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
