package com.timerx.sound

import com.timerx.settings.ITimerXSettings
import org.w3c.dom.Audio
import timerx.shared.generated.resources.Res

class SoundManager(settings: ITimerXSettings) : ISoundManager(settings) {
    override val isTTSSupported: Boolean
        get() = true

    override suspend fun beep(beep: Beep) {
        Audio(beep.uri()).apply { volume = this@SoundManager.volume.toDouble() }.play()
    }

    override suspend fun textToSpeech(text: String) {
        speak(text, volume.toDouble())
    }

    private fun Beep.uri() = Res.getUri("files/$path.mp3")
}

@Suppress("UnusedParameter", "TrimMultilineRawString")
private fun speak(text: String, volume: Double) {
    js(
        """
const utterance = new SpeechSynthesisUtterance(text);
utterance.volume = volume
const voices = speechSynthesis.getVoices();
utterance.voice = voices[0];
speechSynthesis.speak(utterance);
        """
    )
}
