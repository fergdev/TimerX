package com.timerx.ui.create

import androidx.compose.ui.graphics.Color
import com.timerx.sound.Beep
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
        CreateTimerSet(
            id = defaultIdGenerator++,
            repetitions = 5,
            intervals = persistentListOf(
                defaultInterval(workString, green),
                defaultInterval(restString, blue),
            )
        )

    fun defaultInterval(name: String = workString, color: Color = green) =
        CreateTimerInterval(
            id = defaultIdGenerator++,
            name = name,
            duration = 30,
            color = color,
            vibration = Vibration.Medium,
            textToSpeech = true,
            beep = Beep.Alert,
            finalCountDown = CreateFinalCountDown(
                duration = 3,
                beep = Beep.Alert,
                vibration = Vibration.Medium
            )
        )

    fun prepareSet() =
        CreateTimerSet(
            id = defaultIdGenerator++,
            repetitions = 1,
            intervals = persistentListOf(
                CreateTimerInterval(
                    name = "prepare",
                    duration = 10,
                    color = yellow,
                    skipOnLastSet = false,
                    countUp = false,
                    manualNext = false,
                    textToSpeech = true,
                    beep = Beep.Alert,
                    vibration = Vibration.Medium,
                    finalCountDown = CreateFinalCountDown(
                        duration = 3,
                        beep = Beep.Alert,
                        vibration = Vibration.Medium
                    )
                )
            )
        )
}
