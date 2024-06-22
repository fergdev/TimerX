package com.timerx.beep

import android.content.Context
import android.media.MediaPlayer
import com.timerx.R
import com.timerx.beepVibrationDelay
import com.timerx.settings.TimerXSettings
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform

actual fun getBeepManager(timerXSettings: TimerXSettings): IBeepManager =
    BeepManager(timerXSettings)

class BeepManager(private val timerXSettings: TimerXSettings) : IBeepManager {
    private val context: Context = KoinPlatform.getKoin().get()
    private var mediaPlayer: MediaPlayer? = null

    @OptIn(DelicateCoroutinesApi::class)
    override fun beep(beep: Beep) {
        GlobalScope.launch {
            repeat(beep.repeat) {
                mediaPlayer = MediaPlayer.create(context, beep.toResource())
                mediaPlayer?.setOnCompletionListener {
                    it.reset()
                    it.release()
                }
                val volume = timerXSettings.settings.last().volume
                mediaPlayer?.setVolume(volume, volume)
                mediaPlayer?.start()
                delay(beepVibrationDelay)
            }
        }
    }
}

private fun Beep.toResource(): Int {
    return when (this) {
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
}