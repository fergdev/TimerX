package com.timerx.beep

import platform.AudioToolbox.AudioServicesPlayAlertSound

actual fun getBeepMaker(): BeepMaker = BeepMakerImpl()

class BeepMakerImpl : BeepMaker {

    override fun beepNext() {
        AudioServicesPlayAlertSound(1120u)
    }

    override fun beepBack() {
        AudioServicesPlayAlertSound(1111u)
    }

    override fun beepFinished() {
        AudioServicesPlayAlertSound(1112u)
    }

    override fun beepStart() {
        AudioServicesPlayAlertSound(1113u)
    }
}
