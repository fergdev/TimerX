package com.timerx.sound

import com.timerx.settings.ITimerXSettings
import com.timerx.util.NSErrorException
import com.timerx.util.throwNSErrors
import com.timerx.vibration.VIBRATION_DELAY
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryOptionMixWithOthers
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.volume
import platform.Foundation.NSBundle

@OptIn(ExperimentalForeignApi::class)
class SoundManager(timerXSettings: ITimerXSettings) : ISoundManager(timerXSettings) {
    override val isTTSSupported: Boolean
        get() = true

    private val avPlayer = AVPlayer()
    private val synthesizer = AVSpeechSynthesizer()

    init {
        try {
            throwNSErrors {
                val audioSession = AVAudioSession.sharedInstance()
                audioSession.setCategory(
                    AVAudioSessionCategoryPlayback,
                    AVAudioSessionCategoryOptionMixWithOthers,
                    it
                )
                audioSession.setActive(true, it)
            }
        } catch (e: NSErrorException) {
            println("Error setting up audio session: ${e.message}")
        }

        CoroutineScope(Dispatchers.IO).launch {
            timerXSettings.alertSettingsManager.alertSettings.collect {
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
            delay(VIBRATION_DELAY)
        }
    }

    override suspend fun textToSpeech(text: String) {
        val utterance = AVSpeechUtterance(string = text)
        utterance.setVolume(volume)
        synthesizer.speakUtterance(utterance)
    }
}
