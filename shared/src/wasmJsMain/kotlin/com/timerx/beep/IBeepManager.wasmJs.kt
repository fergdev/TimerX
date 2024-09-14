package com.timerx.beep

import com.timerx.settings.ITimerXSettings

actual fun getBeepManager(timerXSettings: ITimerXSettings) = object : IBeepManager {
    override suspend fun beep(beep: Beep) {
        println("Beeeeep $beep")
    }
}