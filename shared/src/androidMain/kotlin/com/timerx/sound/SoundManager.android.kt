@file:Suppress("Filename")

package com.timerx.sound

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import androidx.core.os.bundleOf
import co.touchlab.kermit.Logger
import com.timerx.R
import com.timerx.coroutines.TDispatchers
import com.timerx.settings.AlertSettingsManager
import com.timerx.timermanager.TimerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AndroidSoundManager(
    private val context: Context,
    alertSettingsManager: AlertSettingsManager,
    timerManager: TimerManager,
    tDispatchers: TDispatchers
) : AbstractSoundManager(alertSettingsManager, timerManager, tDispatchers) {
    private var mediaPlayer: MediaPlayer? = null
    private val textToSpeech: TextToSpeech
    private var ttsSupported = false
    override val isTTSSupported: Boolean
        get() = ttsSupported

    init {
        textToSpeech = TextToSpeech(context) { status ->
            ttsSupported = status == TextToSpeech.SUCCESS
            if (status != TextToSpeech.SUCCESS) {
                Logger.e { "There was an error getting text to speech" }
            } else {
                observeVoiceChange(alertSettingsManager)
            }
        }
    }

    private fun observeVoiceChange(alertSettingsManager: AlertSettingsManager) {
        coroutineScope.launch {
            alertSettingsManager.alertSettings.collect { alertSettings ->
                val voice = textToSpeech.voices.firstOrNull { voice ->
                    voice.name == alertSettings.ttsVoiceId
                } ?: return@collect
                textToSpeech.voice = voice
            }
        }
    }

    override suspend fun beep(beep: Beep) {
        repeat(beep.repeat) {
            mediaPlayer = MediaPlayer.create(context, beep.toResource())
            mediaPlayer?.setOnCompletionListener {
                it.reset()
                it.release()
            }
            mediaPlayer?.setVolume(volume.value, volume.value)
            mediaPlayer?.start()
            delay(BEEP_DELAY)
        }
    }

    override suspend fun textToSpeech(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, textToSpeechParams(), text)
    }

    override fun voices(): List<VoiceInformation> {
        if (!ttsSupported) return emptyList()
        return textToSpeech.voices.map {
            VoiceInformation(it.name, it.locale.displayName)
        }
    }

    private fun textToSpeechParams() = bundleOf(
        Pair(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume.value)
    )
}

internal fun Beep.toResource() =
    when (this) {
        Beep.Alert -> R.raw.alert
        Beep.AlertX2 -> R.raw.alert
        Beep.AlertX3 -> R.raw.alert
        Beep.Alert2 -> R.raw.alert2
        Beep.Alert2X2 -> R.raw.alert2
        Beep.Alert2X3 -> R.raw.alert2
        Beep.Whistle -> R.raw.whistle
        Beep.WhistleX2 -> R.raw.whistle
        Beep.WhistleX3 -> R.raw.whistle
        Beep.End -> R.raw.end
        Beep.EndX2 -> R.raw.end
        Beep.EndX3 -> R.raw.end
    }
