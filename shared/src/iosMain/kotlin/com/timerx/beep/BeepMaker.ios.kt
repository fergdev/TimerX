package com.timerx.beep

import platform.AudioToolbox.AudioServicesPlayAlertSound

actual fun getBeepMaker(): BeepMaker = BeepMakerImpl()

class BeepMakerImpl : BeepMaker {

    override fun beepNext() {
        AudioServicesPlayAlertSound(1120u)
    }

    override fun beepPrevious() {
        AudioServicesPlayAlertSound(1111u)
    }

    override fun beepFinished() {
        AudioServicesPlayAlertSound(1112u)
    }

    override fun beepStarted() {
        AudioServicesPlayAlertSound(1113u)
    }
}
