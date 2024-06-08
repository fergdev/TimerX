package com.timerx.beep

expect fun getBeepMaker(volumeManager: VolumeManager): BeepMaker

interface BeepMaker {
    fun beepNext()
    fun beepPrevious()
    fun beepFinished()
    fun beepStarted()
}
