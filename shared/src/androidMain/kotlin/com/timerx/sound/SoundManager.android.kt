package com.timerx.sound

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import androidx.core.os.bundleOf
import com.timerx.R
import com.timerx.settings.ITimerXSettings
import kotlinx.coroutines.delay
import org.koin.mp.KoinPlatform

class SoundManager(timerXSettings: ITimerXSettings, context: Context) :
    ISoundManager(timerXSettings) {
    private val context: Context = KoinPlatform.getKoin().get()
    private var mediaPlayer: MediaPlayer? = null
    private val textToSpeech: TextToSpeech
    private var ttsSupported = false
    override val isTTSSupported: Boolean
        get() = ttsSupported

    init {
        textToSpeech = TextToSpeech(context) { status ->
            ttsSupported = status == TextToSpeech.SUCCESS
            if (status != TextToSpeech.SUCCESS) {
                println("There was an error getting text to speech")
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
            mediaPlayer?.setVolume(volume, volume)
            mediaPlayer?.start()
            delay(BEEP_DELAY)
        }
    }

    override suspend fun textToSpeech(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, textToSpeechParams(), text)
    }

    private fun textToSpeechParams() = bundleOf(
        Pair(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume)
    )
}

private fun Beep.toResource() =
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
