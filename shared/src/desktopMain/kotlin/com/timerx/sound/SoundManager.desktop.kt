@file:Suppress("Filename")

package com.timerx.sound

import com.sun.speech.freetts.Voice
import com.sun.speech.freetts.VoiceManager
import com.timerx.coroutines.TxDispatchers
import com.timerx.settings.AlertSettingsManager
import com.timerx.timermanager.TimerManager
import korlibs.audio.sound.readSound
import korlibs.io.file.std.localVfs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timerx.shared.generated.resources.Res

class DesktopSoundManager(
    alertSettingsManager: AlertSettingsManager,
    timerManager: TimerManager,
    txDispatchers: TxDispatchers
) : AbstractSoundManager(alertSettingsManager, timerManager, txDispatchers) {
    private val voiceManager = VoiceManager.getInstance()
    private var voice: Voice
    override val isTTSSupported: Boolean
        get() = true

    private val voices: List<VoiceInformation>

    init {
        System.setProperty(
            "freetts.voices",
            "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory"
        )
        voices = voiceManager.voices.map { VoiceInformation(it.name, it.name) }
        voice = voiceManager.voices[0].apply { allocate() }

        coroutineScope.launch {
            alertSettingsManager.alertSettings.collect { alertSettings ->
                voiceManager.voices
                    .firstOrNull { it.name == alertSettings.ttsVoiceId }
                    ?.let {
                        voice = it.apply {
                            allocate()
                            volume = alertSettings.volume.value
                        }
                    }
            }
        }
    }

    override suspend fun beep(beep: Beep) {
        val sound = localVfs(beep.localPath()).readSound()
        sound.volume = volume.value.toDouble()
        repeat(beep.repeat) {
            sound.play(coroutineContext = Dispatchers.IO)
            delay(BEEP_DELAY)
        }
    }

    override suspend fun textToSpeech(text: String) {
        withContext(Dispatchers.IO) {
            voice.volume = volume.value
            voice.speak(text)
        }
    }

    override fun voices(): List<VoiceInformation> = voices

    private fun Beep.localPath() = Res.getUri("files/$path.mp3").removePrefix("file:")
}
