package com.intervallum.ui.create

import androidx.compose.ui.graphics.Color
import com.intervallum.sound.Beep
import com.intervallum.ui.common.blue
import com.intervallum.ui.common.green
import com.intervallum.ui.common.yellow
import com.intervallum.vibration.Vibration
import kotlinx.collections.immutable.persistentListOf

internal class DefaultGenerator {
    private var defaultIdGenerator = 0L

    fun getNextId(): Long = defaultIdGenerator++
    fun setMaxId(maxId: Long) {
        defaultIdGenerator = maxId
    }

    fun defaultTimerSet() =
        CreateTimerSet(
            id = defaultIdGenerator++,
            repetitions = 5,
            intervals = persistentListOf(
                defaultInterval("Work", green),
                defaultInterval("Rest", blue),
            )
        )

    fun defaultInterval(name: String = "Work", color: Color = green) =
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
                    name = "Prepare",
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
