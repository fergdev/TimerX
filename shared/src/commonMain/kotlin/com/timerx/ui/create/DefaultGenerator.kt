package com.timerx.ui.create

import androidx.compose.ui.graphics.Color
import com.timerx.beep.Beep
import com.timerx.domain.FinalCountDown
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import com.timerx.vibration.Vibration
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.prepare
import timerx.shared.generated.resources.rest
import timerx.shared.generated.resources.work

internal class DefaultGenerator {
    private var defaultIdGenerator = 0L
    private val workString by lazy {
        runBlocking { getString(Res.string.work) }
    }
    private val restString by lazy {
        runBlocking { getString(Res.string.rest) }
    }

    fun getNextId(): Long = defaultIdGenerator++
    fun defaultTimerSet() =
        TimerSet(
            id = defaultIdGenerator++,
            repetitions = 5,
            intervals = persistentListOf(
                defaultInterval(workString, Color.Green),
                defaultInterval(restString, Color.Blue),
            )
        )

    fun defaultInterval(name: String = workString, color: Color = Color.Green) =
        TimerInterval(
            id = defaultIdGenerator++,
            name = name,
            duration = 30,
            color = color,
            vibration = Vibration.Medium,
            beep = Beep.Alert,
            finalCountDown = FinalCountDown(
                duration = 3,
                beep = Beep.Alert,
                vibration = Vibration.Medium
            )
        )

    fun prepare() =
        TimerSet(
            id = defaultIdGenerator++,
            repetitions = 1,
            intervals = persistentListOf(
                TimerInterval(
                    name = runBlocking { getString(Res.string.prepare) },
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
        )
}
