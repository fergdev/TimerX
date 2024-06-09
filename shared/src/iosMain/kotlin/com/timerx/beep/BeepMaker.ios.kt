package com.timerx.beep

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSBundle

actual fun getBeepMaker(volumeManager: VolumeManager): BeepMaker = BeepMakerImpl(volumeManager)

@OptIn(ExperimentalForeignApi::class)
class BeepMakerImpl(private val volumeManager: VolumeManager) : BeepMaker {

    override fun beepNext() {
        playSound(Beep.Alert)
    }

    override fun beepPrevious() {
        playSound(Beep.Alert2)
    }

    override fun beepFinished() {
        playSound(Beep.End)
    }

    override fun beepStarted() {
        playSound(Beep.Whistle)
    }

    private fun playSound(beep: Beep) {
        val soundURL = NSBundle.mainBundle.URLForResource(beep.path, "mp3")
        if (soundURL == null) {
            println("Sound not found ${beep}")
            return
        }
        val avAudioPlayer = AVAudioPlayer(contentsOfURL = soundURL, error = null)
        avAudioPlayer.setVolume(volumeManager.getVolume())
        avAudioPlayer.play()
    }

}
