package com.timerx.beep

import com.timerx.settings.ITimerXSettings
import com.timerx.util.throwNSErrors
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.volume
import platform.Foundation.NSBundle

@OptIn(ExperimentalForeignApi::class)
class BeepManager(private val timerXSettings: ITimerXSettings) : IBeepManager {
    private val avPlayer = AVPlayer()
    init {
        try {
            throwNSErrors {
                val audioSession = AVAudioSession.sharedInstance()
                audioSession.setCategory(AVAudioSessionCategoryPlayback, it)
                audioSession.setActive(true, it)
            }
        } catch (e: Exception) {
            println("Error setting up audio session: ${e.message}")
        }

        CoroutineScope(Dispatchers.IO).launch {
            timerXSettings.settings.collect {
                avPlayer.volume = it.volume
            }
        }
    }

    override suspend fun beep(beep: Beep) {
        val soundURL = NSBundle.mainBundle.URLForResource(beep.path, "mp3")
        if (soundURL == null) {
            println("Beep not found $beep")
            return
        }
        repeat(beep.repeat) {
            avPlayer.replaceCurrentItemWithPlayerItem(AVPlayerItem(soundURL))
            avPlayer.play()
            avPlayer.error?.let {
                println("AVPlayer error? = ${it.localizedDescription}")
            }
            delay(BEEP_VIBRATION_DELAY)
        }
    }
}
