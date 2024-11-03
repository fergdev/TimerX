@file:Suppress("Filename")

package com.timerx.sound

import co.touchlab.kermit.Logger
import com.timerx.coroutines.TDispatchers
import com.timerx.settings.AlertSettingsManager
import com.timerx.timermanager.TimerManager
import com.timerx.util.NSErrorException
import com.timerx.util.throwNSErrors
import com.timerx.vibration.VIBRATION_DELAY
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryOptionMixWithOthers
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVSpeechSynthesisVoice
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance
import platform.AVFAudio.setActive
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerTimeControlStatusPlaying
import platform.AVFoundation.play
import platform.AVFoundation.replaceCurrentItemWithPlayerItem
import platform.AVFoundation.timeControlStatus
import platform.AVFoundation.volume
import platform.Foundation.NSBundle

@OptIn(ExperimentalForeignApi::class)
class IosSoundManager(
    alertSettingsManager: AlertSettingsManager,
    timerManager: TimerManager,
    tDispatchers: TDispatchers
) : AbstractSoundManager(alertSettingsManager, timerManager, tDispatchers) {
    override val isTTSSupported: Boolean
        get() = true

    private val avPlayer = AVPlayer()
    private val synthesizer = AVSpeechSynthesizer()
    private var voice: AVSpeechSynthesisVoice? = null
    private val voiceInformation: List<VoiceInformation>

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
            Logger.e { "Error setting up audio session: ${e.message}" }
        }

        voiceInformation = mappedVoices().map {
            VoiceInformation(
                it.identifier,
                "${it.language} - ${it.name}"
            )
        }

        coroutineScope.launch {
            alertSettingsManager.alertSettings.collect { alertSettings ->
                avPlayer.volume = alertSettings.volume.value
                voice = mappedVoices().firstOrNull {
                    it.identifier == alertSettings.ttsVoiceId
                }
            }
        }
    }

    override suspend fun beep(beep: Beep) {
        val soundURL = NSBundle.mainBundle.URLForResource(beep.path, "mp3")
        if (soundURL == null) {
            Logger.e { "Beep not found $beep" }
            return
        }
        repeat(beep.repeat) {
            avPlayer.replaceCurrentItemWithPlayerItem(AVPlayerItem(soundURL))
            avPlayer.play()
            avPlayer.error?.let {
                Logger.e { "AVPlayer error? = ${it.localizedDescription}" }
            }
            delay(VIBRATION_DELAY)
        }
    }

    override suspend fun textToSpeech(text: String) {
        val utterance = AVSpeechUtterance(string = text)
        utterance.setVolume(volume.value)
        utterance.voice = voice
        synthesizer.speakUtterance(utterance)
    }

    fun playEmptySound() {
        val isPlaying = avPlayer.timeControlStatus() == AVPlayerTimeControlStatusPlaying ||
            synthesizer.isSpeaking()
        Logger.d { "is playing sound $isPlaying" }
        avPlayer.status()
        if (!isPlaying) {
            val soundURL = NSBundle.mainBundle.URLForResource("silence", "mp3")!!
            val item = AVPlayerItem(soundURL)
            avPlayer.replaceCurrentItemWithPlayerItem(item)
            avPlayer.play()
            avPlayer.error?.let {
                Logger.e { "AVPlayer error? = ${it.localizedDescription}" }
            }
        }
    }

    override fun voices(): List<VoiceInformation> = voiceInformation

    private fun mappedVoices() = AVSpeechSynthesisVoice.speechVoices().map {
        it as AVSpeechSynthesisVoice
    }
}
