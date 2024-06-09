package com.timerx.beep

import android.content.Context
import android.media.MediaPlayer
import com.timerx.R
import com.timerx.settings.TimerXSettings
import org.koin.mp.KoinPlatform

actual fun getBeepMaker(timerXSettings: TimerXSettings): BeepMaker = BeepMakerImpl(timerXSettings)

class BeepMakerImpl(val timerXSettings: TimerXSettings) : BeepMaker {
    private val context: Context = KoinPlatform.getKoin().get()
    private var mediaPlayer: MediaPlayer? = null

    private fun playSound(beep: Beep) {
        mediaPlayer = MediaPlayer.create(context, beep.toResource())
        mediaPlayer?.setOnCompletionListener {
            it.release()
        }
        val volume = timerXSettings.volume
        println("Playing $beep with $volume")
        mediaPlayer?.setVolume(volume, volume)
        mediaPlayer?.start()
    }

    override fun beepStarted() {
        playSound(Beep.Whistle)
    }

    override fun beepNext() {
        playSound(Beep.Alert)
    }

    override fun beepPrevious() {
        playSound(Beep.Alert2)
    }

    override fun beepFinished() {
        playSound(Beep.End)
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