package com.timerx.sound

import com.sun.speech.freetts.Voice
import com.sun.speech.freetts.VoiceManager
import com.timerx.settings.TimerXSettings
import korlibs.audio.sound.readSound
import korlibs.io.file.std.localVfs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timerx.shared.generated.resources.Res

class SoundManager(timerXSettings: TimerXSettings) : ISoundManager(timerXSettings) {
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
            timerXSettings.alertSettingsManager.alertSettings.collect { alertSettings ->
                voiceManager.voices
                    .firstOrNull { it.name == alertSettings.ttsVoiceName }
                    ?.let {
                        voice = it.apply {
                            allocate()
                            volume = alertSettings.volume
                        }
                    }
            }
        }
    }

    override suspend fun beep(beep: Beep) {
        val sound = localVfs(beep.localPath()).readSound()
        sound.volume = volume.toDouble()
        repeat(beep.repeat) {
            sound.play(coroutineContext = Dispatchers.IO)
            delay(BEEP_DELAY)
        }
    }

    override suspend fun textToSpeech(text: String) {
        withContext(Dispatchers.IO) {
            voice.volume = volume
            voice.speak(text)
        }
    }

    override fun voices(): List<VoiceInformation> = voices

    private fun Beep.localPath() = Res.getUri("files/$path.mp3").removePrefix("file:")
}