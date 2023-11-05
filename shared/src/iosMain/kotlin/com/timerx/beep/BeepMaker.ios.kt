package com.timerx.beep

actual fun getBeepMaker(): BeepMaker = BeepMakerImpl()

class BeepMakerImpl : BeepMaker {
    override fun beepNext() {
        println("Beep next")
    }

    override fun beepBack() {
        println("Beep back")
    }

    override fun beepFinished() {
        println("Beep finished")
    }

    override fun beepStart() {
        println("Beep start")
    }
}
