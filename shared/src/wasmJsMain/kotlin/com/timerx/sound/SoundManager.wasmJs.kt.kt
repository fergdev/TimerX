package com.timerx.sound

import com.timerx.settings.ITimerXSettings
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.w3c.dom.Audio
import timerx.shared.generated.resources.Res

private const val DEFAULT_VOICE_ID = "null"

class SoundManager(settings: ITimerXSettings) : ISoundManager(settings) {
    private var selectedVoiceId: String = DEFAULT_VOICE_ID
    override val isTTSSupported: Boolean
        get() = true

    init {
        coroutineScope.launch {
            settings.alertSettingsManager.alertSettings.collect {
                selectedVoiceId = it.ttsVoiceName ?: DEFAULT_VOICE_ID
            }
        }
    }

    override suspend fun beep(beep: Beep) {
        Audio(beep.uri()).apply { volume = this@SoundManager.volume.toDouble() }.play()
    }

    override suspend fun textToSpeech(text: String) {
        speak(text = text, volume = volume.toDouble(), voiceId = selectedVoiceId)
    }

    // This should be cached, but there is an issue with JS where
    // this block returns empty when invoked too soon
    override fun voices(): List<VoiceInformation> =
        Json.decodeFromString<List<VoiceInformation>>(jsVoices())

    private fun Beep.uri() = Res.getUri("files/$path.mp3")
}

@Suppress("UnusedParameter", "TrimMultilineRawString", "RedundantSuppression", "Unused")
private fun speak(text: String, volume: Double, voiceId: String) {
    js(
        """
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.volume = volume
    const voices = speechSynthesis.getVoices();
    let voice = voices.find((x) => x.voiceURI == voiceId)
    if (voice == undefined) {
        utterance.voice = voices[0]
    } else {
        utterance.voice = voice
    }
    speechSynthesis.speak(utterance);
"""
    )
}

@Suppress("TrimMultilineRawString")
private fun jsVoices(): String = js(
    """ {
    return JSON.stringify(speechSynthesis.getVoices().map((x) => {return { name: x.name, id: x.voiceURI }} ));
} """
)