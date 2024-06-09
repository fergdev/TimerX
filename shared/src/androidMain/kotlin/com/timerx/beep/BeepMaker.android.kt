package com.timerx.beep

import android.content.Context
import android.media.MediaPlayer
import com.timerx.R
import com.timerx.settings.TimerXSettings
import org.koin.mp.KoinPlatform

actual fun getBeepMaker(timerXSettings: TimerXSettings): BeepMaker = BeepMakerImpl(timerXSettings)

class BeepMakerImpl(private val timerXSettings: TimerXSettings) : BeepMaker {
    private val context: Context = KoinPlatform.getKoin().get()
    private var mediaPlayer: MediaPlayer? = null

    override fun beep(beep: Beep) {
        mediaPlayer = MediaPlayer.create(context, beep.toResource())
        mediaPlayer?.setOnCompletionListener {
            it.reset()
            it.release()
        }
        val volume = timerXSettings.volume
        mediaPlayer?.setVolume(volume, volume)
        mediaPlayer?.start()
    }
}

private fun Beep.toResource(): Int {
    return when (this) {
        Beep.Alert -> R.raw.alert
        Beep.Whistle -> R.raw.whistle
        Beep.Alert2 -> R.raw.alert2
        Beep.End -> R.raw.end
    }
}