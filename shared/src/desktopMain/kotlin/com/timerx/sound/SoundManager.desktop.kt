package com.timerx.sound

import com.sun.speech.freetts.Voice
import com.sun.speech.freetts.VoiceManager
import com.timerx.settings.TimerXSettings
import korlibs.audio.sound.readSound
import korlibs.io.file.std.localVfs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timerx.shared.generated.resources.Res

class SoundManager(timerXSettings: TimerXSettings) : ISoundManager(timerXSettings) {
    private val voiceManager = VoiceManager.getInstance()
    private val voice: Voice
    override val isTTSSupported: Boolean
        get() = true

    init {
        System.setProperty(
            "freetts.voices",
            "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory"
        )
        voice = voiceManager.voices[1]
        voice.allocate()
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

    private fun Beep.localPath() = Res.getUri("files/$path.mp3").removePrefix("file:")
}
