package com.timerx.beep

expect fun getBeepMaker(): BeepMaker

interface BeepMaker {
    fun beepNext()
    fun beepPrevious()
    fun beepFinished()
    fun beepStarted()
}
