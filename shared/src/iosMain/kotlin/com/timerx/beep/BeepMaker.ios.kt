package com.timerx.beep

import com.timerx.settings.TimerXSettings
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSBundle

actual fun getBeepManager(timerXSettings: TimerXSettings): IBeepManager = BeepManager(timerXSettings)

@OptIn(ExperimentalForeignApi::class)
class BeepManager(private val timerXSettings: TimerXSettings) : IBeepManager {

    override fun beep(beep: Beep) {
        val soundURL = NSBundle.mainBundle.URLForResource(beep.path, "mp3")
        if (soundURL == null) {
            println("Sound not found ${beep}")
            return
        }
        val avAudioPlayer = AVAudioPlayer(contentsOfURL = soundURL, error = null)
        avAudioPlayer.setVolume(timerXSettings.volume)
        avAudioPlayer.play()
    }
}