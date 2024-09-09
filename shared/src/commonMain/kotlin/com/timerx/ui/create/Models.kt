package com.timerx.ui.create

import androidx.compose.ui.graphics.Color
import com.timerx.beep.Beep
import com.timerx.domain.FinalCountDown
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import com.timerx.vibration.Vibration
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import pro.respawn.flowmvi.api.MVIAction
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

internal data class CreateScreenState(
    val timerName: String = "",
    val finishColor: Color = Color.Red,
    val finishBeep: Beep = Beep.End,
    val finishVibration: Vibration = Vibration.Heavy,
    val sets: PersistentList<TimerSet> = persistentListOf()
) : MVIState

sealed interface CreateScreenIntent : MVIIntent {
    data class UpdateTimerName(val timerName: String) : CreateScreenIntent
    data object Save : CreateScreenIntent

    data class UpdateFinishColor(val color: Color) : CreateScreenIntent
    data class UpdateFinishBeep(val beep: Beep) : CreateScreenIntent
    data class UpdateFinishVibration(val vibration: Vibration) : CreateScreenIntent

    data object AddSet : CreateScreenIntent
    data class SwapSet(val from: Int, val to: Int) : CreateScreenIntent
    data class DuplicateSet(val set: TimerSet) : CreateScreenIntent
    data class DeleteSet(val set: TimerSet) : CreateScreenIntent

    data class NewInterval(val set: TimerSet) : CreateScreenIntent
    data class MoveInterval(val set: TimerSet, val from: Int, val to: Int) : CreateScreenIntent
    data class UpdateSetRepetitions(val set: TimerSet, val repetitions: Int) : CreateScreenIntent

    data class DeleteInterval(val interval: TimerInterval) : CreateScreenIntent
    data class DuplicateInterval(val interval: TimerInterval) : CreateScreenIntent

    data class UpdateIntervalDuration(val interval: TimerInterval, val duration: Int) :
        CreateScreenIntent

    data class UpdateIntervalName(val interval: TimerInterval, val name: String) :
        CreateScreenIntent

    data class UpdateIntervalColor(val interval: TimerInterval, val color: Color) :
        CreateScreenIntent

    data class UpdateIntervalSkipOnLastSet(
        val interval: TimerInterval,
        val skipOnLastSet: Boolean
    ) : CreateScreenIntent

    data class UpdateIntervalCountUp(val interval: TimerInterval, val countUp: Boolean) :
        CreateScreenIntent

    data class UpdateIntervalManualNext(val interval: TimerInterval, val manualNext: Boolean) :
        CreateScreenIntent

    data class UpdateIntervalBeep(val interval: TimerInterval, val beep: Beep) : CreateScreenIntent
    data class UpdateIntervalFinalCountDown(
        val interval: TimerInterval,
        val finalCountDown: FinalCountDown
    ) : CreateScreenIntent

    data class UpdateIntervalVibration(val interval: TimerInterval, val vibration: Vibration) :
        CreateScreenIntent
}

internal sealed interface RunScreenAction : MVIAction {
    data object NavigateUp : RunScreenAction
}