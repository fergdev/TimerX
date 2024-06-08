package com.timerx.beep

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSBundle

actual fun getBeepMaker(volumeManager: VolumeManager): BeepMaker = BeepMakerImpl(volumeManager)

@OptIn(ExperimentalForeignApi::class)
class BeepMakerImpl(private val volumeManager: VolumeManager) : BeepMaker {

    override fun beepNext() {
        playSound()
    }

    override fun beepPrevious() {
        playSound()
    }

    override fun beepFinished() {
        playSound()
    }

    override fun beepStarted() {
        playSound()
    }

    private fun playSound() {
        val soundURL = NSBundle.mainBundle.URLForResource("alert", "mp3")
        if (soundURL == null) {
            println("Sound not found")
            return
        }
        val avAudioPlayer = AVAudioPlayer(contentsOfURL = soundURL, error = null)
        avAudioPlayer.setVolume(volumeManager.getVolume())
        avAudioPlayer.play()
    }

}
