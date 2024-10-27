package com.timerx.ui.create

import androidx.compose.ui.graphics.Color
import com.timerx.sound.Beep
import com.timerx.vibration.Vibration
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

data class TimerNameState(
    val name: String = "",
    val isError: Boolean = false
)

data class CreateScreenState(
    val timerNameState: TimerNameState = TimerNameState(),
    val finishColor: Color = Color.Red,
    val finishBeep: Beep = Beep.End,
    val finishVibration: Vibration = Vibration.Heavy,
    val isEditing: Boolean = false,
    val canVibrate: Boolean = false,
    val sets: PersistentList<CreateTimerSet> = persistentListOf()
) : MVIState

sealed interface CreateScreenIntent : MVIIntent {
    data class UpdateTimerName(val timerName: String) : CreateScreenIntent
    data object Save : CreateScreenIntent

    data class UpdateFinishColor(val color: Color) : CreateScreenIntent
    data class UpdateFinishBeep(val beep: Beep) : CreateScreenIntent
    data class UpdateFinishVibration(val vibration: Vibration) : CreateScreenIntent

    data object AddSet : CreateScreenIntent
    data class SwapSet(val from: Int, val to: Int) : CreateScreenIntent
    data class DuplicateSet(val set: CreateTimerSet) : CreateScreenIntent
    data class DeleteSet(val set: CreateTimerSet) : CreateScreenIntent

    data class NewInterval(val set: CreateTimerSet) : CreateScreenIntent
    data class MoveInterval(val set: CreateTimerSet, val from: Int, val to: Int) :
        CreateScreenIntent

    data class UpdateSetRepetitions(val set: CreateTimerSet, val repetitions: Int) :
        CreateScreenIntent

    data class DeleteInterval(val interval: CreateTimerInterval) : CreateScreenIntent
    data class DuplicateInterval(val interval: CreateTimerInterval) : CreateScreenIntent

    data class UpdateIntervalDuration(val interval: CreateTimerInterval, val duration: Long) :
        CreateScreenIntent

    data class UpdateIntervalName(val interval: CreateTimerInterval, val name: String) :
        CreateScreenIntent

    data class UpdateIntervalColor(val interval: CreateTimerInterval, val color: Color) :
        CreateScreenIntent

    data class UpdateIntervalSkipOnLastSet(
        val interval: CreateTimerInterval,
        val skipOnLastSet: Boolean
    ) : CreateScreenIntent

    data class UpdateIntervalCountUp(val interval: CreateTimerInterval, val countUp: Boolean) :
        CreateScreenIntent

    data class UpdateIntervalManualNext(
        val interval: CreateTimerInterval,
        val manualNext: Boolean
    ) :
        CreateScreenIntent

    data class UpdateIntervalBeep(val interval: CreateTimerInterval, val beep: Beep) :
        CreateScreenIntent

    data class UpdateIntervalFinalCountDown(
        val interval: CreateTimerInterval,
        val finalCountDown: CreateFinalCountDown
    ) : CreateScreenIntent

    data class UpdateIntervalVibration(
        val interval: CreateTimerInterval,
        val vibration: Vibration
    ) :
        CreateScreenIntent

    data class UpdateIntervalTextToSpeech(
        val interval: CreateTimerInterval,
        val textToSpeech: Boolean
    ) :
        CreateScreenIntent
}

interface CreateAction : MVIAction {
    data class TimerUpdated(val timerId: Long) : CreateAction
    data object EmptyTimerAction : CreateAction
}

data class CreateTimerSet(
    val id: Long = 0L,
    val repetitions: Int = 1,
    val intervals: PersistentList<CreateTimerInterval>
)

data class CreateTimerInterval(
    val id: Long = 0L,
    val name: String,
    val duration: Long,
    val color: Color = Color.Blue,
    val skipOnLastSet: Boolean = false,
    val countUp: Boolean = false,
    val manualNext: Boolean = false,
    val textToSpeech: Boolean = true,
    val beep: Beep = Beep.Alert,
    val vibration: Vibration = Vibration.Medium,
    val finalCountDown: CreateFinalCountDown = CreateFinalCountDown()
)

data class CreateFinalCountDown(
    val duration: Long = 3,
    val beep: Beep = Beep.Alert,
    val vibration: Vibration = Vibration.Light
)

fun List<CreateTimerSet>.length() =
    fold(0L) { acc, timerSet ->
        acc + timerSet.length()
    }

fun CreateTimerSet.length() =
    intervals.fold(0L) { acc, interval ->
        acc + interval.length()
    } * repetitions

fun CreateTimerInterval.length() = duration
