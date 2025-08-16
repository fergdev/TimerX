package com.intervallum.ui.create

import androidx.compose.ui.graphics.Color
import com.intervallum.database.ITimerRepository
import com.intervallum.domain.FinalCountDown
import com.intervallum.domain.Timer
import com.intervallum.domain.TimerInterval
import com.intervallum.domain.TimerSet
import com.intervallum.platform.platformCapabilities
import com.intervallum.sound.Beep
import com.intervallum.sound.SoundManager
import com.intervallum.ui.create.CreateAction.TimerUpdated
import com.intervallum.ui.create.CreateScreenIntent.AddSet
import com.intervallum.ui.create.CreateScreenIntent.DeleteInterval
import com.intervallum.ui.create.CreateScreenIntent.DeleteSet
import com.intervallum.ui.create.CreateScreenIntent.DuplicateInterval
import com.intervallum.ui.create.CreateScreenIntent.DuplicateSet
import com.intervallum.ui.create.CreateScreenIntent.MoveInterval
import com.intervallum.ui.create.CreateScreenIntent.NewInterval
import com.intervallum.ui.create.CreateScreenIntent.Save
import com.intervallum.ui.create.CreateScreenIntent.SwapSet
import com.intervallum.ui.create.CreateScreenIntent.UpdateFinishBeep
import com.intervallum.ui.create.CreateScreenIntent.UpdateFinishColor
import com.intervallum.ui.create.CreateScreenIntent.UpdateFinishVibration
import com.intervallum.ui.create.CreateScreenIntent.UpdateIntervalBeep
import com.intervallum.ui.create.CreateScreenIntent.UpdateIntervalColor
import com.intervallum.ui.create.CreateScreenIntent.UpdateIntervalCountUp
import com.intervallum.ui.create.CreateScreenIntent.UpdateIntervalDuration
import com.intervallum.ui.create.CreateScreenIntent.UpdateIntervalFinalCountDown
import com.intervallum.ui.create.CreateScreenIntent.UpdateIntervalManualNext
import com.intervallum.ui.create.CreateScreenIntent.UpdateIntervalName
import com.intervallum.ui.create.CreateScreenIntent.UpdateIntervalSkipOnLastSet
import com.intervallum.ui.create.CreateScreenIntent.UpdateIntervalTextToSpeech
import com.intervallum.ui.create.CreateScreenIntent.UpdateIntervalVibration
import com.intervallum.ui.create.CreateScreenIntent.UpdateSetRepetitions
import com.intervallum.ui.create.CreateScreenIntent.UpdateTimerName
import com.intervallum.ui.di.ConfigurationFactory
import com.intervallum.ui.di.configure
import com.intervallum.vibration.Vibration
import com.intervallum.vibration.VibrationManager
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import pro.respawn.flowmvi.api.Container
import pro.respawn.flowmvi.dsl.store
import pro.respawn.flowmvi.plugins.init
import pro.respawn.flowmvi.plugins.reducePlugin
import kotlin.math.max
import kotlin.time.Clock

class CreateContainer(
    timerId: Long,
    configurationFactory: ConfigurationFactory,
    private val timerDatabase: ITimerRepository,
    private val soundManager: SoundManager,
    private val vibrationManger: VibrationManager,
) : Container<CreateScreenState, CreateScreenIntent, CreateAction> {
    private val defaultGenerator = DefaultGenerator()
    override val store = store(CreateScreenState(canVibrate = platformCapabilities.canVibrate)) {
        configure(configurationFactory, "Create")
        init {
            launch {
                val timer = timerDatabase.getTimer(timerId).first()
                if (timer != null) {
                    updateState {
                        defaultGenerator.setMaxId(timer.sets.getMaxId())
                        CreateScreenState(
                            timerNameState = TimerNameState(timer.name),
                            sets = timer.sets.toCreateTimerSet(),
                            isEditing = true,
                            canVibrate = platformCapabilities.canVibrate,
                            finishColor = timer.finishColor,
                            finishBeep = timer.finishBeep,
                            finishVibration = timer.finishVibration,
                        )
                    }
                } else {
                    updateState {
                        CreateScreenState(
                            canVibrate = platformCapabilities.canVibrate,
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
                soundManager,
                vibrationManger,
                timerId,
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

@Suppress("LongMethod", "CyclomaticComplexMethod")
private fun reduceIntent(
    defaultGenerator: DefaultGenerator,
    timerDatabase: ITimerRepository,
    beepManager: SoundManager,
    vibrationManger: VibrationManager,
    timerId: Long,
) = reducePlugin<CreateScreenState, CreateScreenIntent, CreateAction> {
    when (it) {
        is UpdateTimerName -> updateState { copy(timerNameState = TimerNameState(name = it.timerName)) }
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
                        if (set.intervals.any { interval -> interval.id == it.interval.id }) {
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

        is UpdateFinishColor -> updateState { copy(finishColor = it.color) }

        is UpdateFinishVibration -> {
            updateState { copy(finishVibration = it.vibration) }
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

        is UpdateIntervalTextToSpeech -> {
            updateState {
                copy(
                    sets = sets.updateInterval(
                        interval = it.interval,
                        textToSpeech = it.textToSpeech
                    )
                )
            }
        }

        Save -> {
            withState {
                if (timerNameState.name.isBlank()) {
                    updateState {
                        copy(timerNameState = timerNameState.copy(isError = true))
                    }
                    return@withState
                }
                val newSets = sets.normaliseSets()
                if (newSets.isEmpty()) {
                    action(CreateAction.EmptyTimerAction)
                    return@withState
                }
                launch {
                    val timerEditing = timerDatabase.getTimer(timerId).first()
                    if (timerEditing != null) {
                        timerDatabase.updateTimer(
                            Timer(
                                id = timerEditing.id,
                                sortOrder = timerEditing.sortOrder,
                                name = timerNameState.name,
                                sets = newSets,
                                finishColor = finishColor,
                                finishBeep = finishBeep,
                                finishVibration = finishVibration,
                                startedCount = timerEditing.startedCount,
                                completedCount = timerEditing.completedCount,
                                createdAt = timerEditing.createdAt
                            )
                        )
                        action(TimerUpdated(timerId))
                    } else {
                        val id = timerDatabase.insertTimer(
                            Timer(
                                name = timerNameState.name,
                                sets = newSets,
                                finishColor = finishColor,
                                finishBeep = finishBeep,
                                finishVibration = finishVibration,
                                createdAt = Clock.System.now()
                            )
                        )
                        action(TimerUpdated(id))
                    }
                }
            }
        }
    }
}

private fun List<TimerSet>.toCreateTimerSet() =
    this.map { timerSet ->
        CreateTimerSet(
            id = timerSet.id,
            repetitions = timerSet.repetitions,
            intervals = timerSet.intervals.map { interval ->
                interval.toCreateTimerInterval()
            }.toPersistentList()
        )
    }.toPersistentList()

private fun TimerInterval.toCreateTimerInterval() =
    CreateTimerInterval(
        id = id,
        name = name,
        duration = duration,
        color = color,
        skipOnLastSet = skipOnLastSet,
        countUp = countUp,
        manualNext = manualNext,
        textToSpeech = textToSpeech,
        beep = beep,
        vibration = vibration,
        finalCountDown = finalCountDown.toCreateFinalCountDown(),
    )

private fun FinalCountDown.toCreateFinalCountDown() =
    CreateFinalCountDown(
        duration = duration,
        beep = beep,
        vibration = vibration
    )

private fun CreateFinalCountDown.toFinalCountDown() =
    FinalCountDown(
        duration = duration,
        beep = beep,
        vibration = vibration
    )

private fun List<CreateTimerSet>.normaliseSets() =
    this.filter { set -> set.intervals.isNotEmpty() }
        .map { set ->
            TimerSet(
                id = 0,
                repetitions = set.repetitions,
                intervals = set.intervals.map { interval ->
                    TimerInterval(
                        id = 0,
                        name = interval.name,
                        duration = interval.duration,
                        color = interval.color,
                        skipOnLastSet = interval.skipOnLastSet,
                        countUp = interval.countUp,
                        manualNext = interval.manualNext,
                        textToSpeech = interval.textToSpeech,
                        beep = interval.beep,
                        vibration = interval.vibration,
                        finalCountDown = interval.finalCountDown.toFinalCountDown(),
                    )
                }
            )
        }

private fun PersistentList<CreateTimerSet>.updateInterval(
    interval: CreateTimerInterval,
    name: String? = null,
    duration: Long? = null,
    color: Color? = null,
    skipOnLastSet: Boolean? = null,
    countUp: Boolean? = null,
    manualNext: Boolean? = null,
    textToSpeech: Boolean? = null,
    beep: Beep? = null,
    vibration: Vibration? = null,
    finalCountDown: CreateFinalCountDown? = null
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
            textToSpeech = textToSpeech ?: interval.textToSpeech,
            beep = beep ?: interval.beep,
            vibration = vibration ?: interval.vibration,
            finalCountDown = finalCountDown ?: interval.finalCountDown
        )
        set.copy(intervals = mutableIntervals.toPersistentList())
    } else {
        set
    }
}.toPersistentList()
