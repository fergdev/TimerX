package com.timerx.beep

import android.content.Context
import android.media.MediaPlayer
import com.timerx.R
import com.timerx.settings.ITimerXSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform

class BeepManager(private val timerXSettings: ITimerXSettings) : IBeepManager {
    private val context: Context = KoinPlatform.getKoin().get()
    private var mediaPlayer: MediaPlayer? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var volume: Float = 1f

    init {
        coroutineScope.launch {
            timerXSettings.alertSettingsManager.alertSettings.collect { volume = it.volume }
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
            delay(BEEP_VIBRATION_DELAY)
        }
    }
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
