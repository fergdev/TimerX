package com.timerx.ui.create

import androidx.compose.ui.graphics.Color
import com.timerx.sound.Beep
import com.timerx.domain.FinalCountDown
import com.timerx.domain.TimerInterval
import com.timerx.domain.TimerSet
import com.timerx.ui.common.blue
import com.timerx.ui.common.green
import com.timerx.ui.common.yellow
import com.timerx.vibration.Vibration
import kotlinx.collections.immutable.persistentListOf

internal class DefaultGenerator {
    private var defaultIdGenerator = 0L

    private val workString by lazy { "work" }
    private val restString by lazy { "rest" }

    fun getNextId(): Long = defaultIdGenerator++
    fun setMaxId(maxId: Long) {
        defaultIdGenerator = maxId
    }

    fun defaultTimerSet() =
        TimerSet(
            id = defaultIdGenerator++,
            repetitions = 5,
            intervals = persistentListOf(
                defaultInterval(workString, green),
                defaultInterval(restString, blue),
            )
        )

    fun defaultInterval(name: String = workString, color: Color = green) =
        TimerInterval(
            id = defaultIdGenerator++,
            name = name,
            duration = 30,
            color = color,
            vibration = Vibration.Medium,
            textToSpeech = true,
            beep = Beep.Alert,
            finalCountDown = FinalCountDown(
                duration = 3,
                beep = Beep.Alert,
                vibration = Vibration.Medium
            )
        )

    fun prepareSet() =
        TimerSet(
            id = defaultIdGenerator++,
            repetitions = 1,
            intervals = persistentListOf(
                TimerInterval(
                    name = "prepare",
                    duration = 10,
                    color = yellow,
                    skipOnLastSet = false,
                    countUp = false,
                    manualNext = false,
                    textToSpeech = true,
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
