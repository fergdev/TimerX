package com.timerx.beep

import com.timerx.BEEP_VIBRATION_DELAY
import com.timerx.settings.TimerXSettings
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSBundle

actual fun getBeepManager(timerXSettings: TimerXSettings): IBeepManager =
    BeepManager(timerXSettings)

@OptIn(ExperimentalForeignApi::class)
class BeepManager(private val timerXSettings: TimerXSettings) : IBeepManager {

    override suspend fun beep(beep: Beep) {
        val soundURL = NSBundle.mainBundle.URLForResource(beep.path, "mp3")
        if (soundURL == null) {
            println("Beep not found $beep")
            return
        }

        repeat(beep.repeat) {
            val avAudioPlayer = AVAudioPlayer(contentsOfURL = soundURL, error = null)
            avAudioPlayer.setVolume(timerXSettings.settings.first().volume)
            avAudioPlayer.play()
            delay(BEEP_VIBRATION_DELAY)
        }
    }
}