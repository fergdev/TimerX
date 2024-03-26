package com.timerx.beep

expect fun getBeepMaker(): BeepMaker

interface BeepMaker {
    fun beepNext()
    fun beepBack()
    fun beepFinished()
    fun beepStart()
}
