package com.timerx.beep

import com.timerx.beepVibrationDelay
import com.timerx.settings.TimerXSettings
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSBundle

actual fun getBeepManager(timerXSettings: TimerXSettings): IBeepManager = BeepManager(timerXSettings)

@OptIn(ExperimentalForeignApi::class)
class BeepManager(private val timerXSettings: TimerXSettings) : IBeepManager {

    @OptIn(DelicateCoroutinesApi::class)
    override fun beep(beep: Beep) {
        val soundURL = NSBundle.mainBundle.URLForResource(beep.path, "mp3")
        if (soundURL == null) {
            println("Beep not found $beep")
            return
        }

        GlobalScope.launch {
            repeat(beep.repeat){
                val avAudioPlayer = AVAudioPlayer(contentsOfURL = soundURL, error = null)
                avAudioPlayer.setVolume(timerXSettings.volume)
                avAudioPlayer.play()
                delay(beepVibrationDelay)
            }
        }
    }
}