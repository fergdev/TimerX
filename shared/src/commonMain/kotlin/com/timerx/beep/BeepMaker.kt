package com.timerx.beep

import com.timerx.settings.TimerXSettings

expect fun getBeepMaker(timerXSettings: TimerXSettings): BeepMaker

interface BeepMaker {
    fun beepNext()
    fun beepPrevious()
    fun beepFinished()
    fun beepStarted()
}
