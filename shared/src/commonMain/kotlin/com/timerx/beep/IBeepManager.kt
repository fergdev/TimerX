package com.timerx.beep

import com.timerx.settings.TimerXSettings

expect fun getBeepManager(timerXSettings: TimerXSettings): IBeepManager

interface IBeepManager {
    fun beep(beep: Beep)
}
