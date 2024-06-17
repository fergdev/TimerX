package com.timerx.beep

import com.timerx.settings.TimerXSettings

expect fun getBeepManager(timerXSettings: TimerXSettings): IBeepManager

interface IBeepManager {
    fun beep(beep: Beep)
}

enum class Beep(val displayName: String, val path: String, val repeat: Int) {
    Alert("Alert", "alert", 1),
    AlertX2("Alert X2", "alert", 2),
    AlertX3("Alert X3", "alert", 3),
    Alert2("Alert2", "alert2", 1),
    Alert2X2("Alert2 X2", "alert2", 2),
    Alert2X3("Alert2 X3", "alert2", 3),
    Whistle("Whistle", "whistle", 1),
    WhistleX2("Whistle X2", "whistle", 2),
    WhistleX3("Whistle X3", "whistle", 3),
    End("End", "end", 1),
    EndX2("End X2", "end", 2),
    EndX3("End X3", "end", 3)
}
