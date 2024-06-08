package com.timerx.beep

import android.content.Context
import android.media.MediaPlayer
import com.timerx.R
import org.koin.mp.KoinPlatform

actual fun getBeepMaker(volumeManager: VolumeManager): BeepMaker = BeepMakerImpl(volumeManager)

class BeepMakerImpl(private val volumeManager: VolumeManager) : BeepMaker {
    private val context: Context = KoinPlatform.getKoin().get()
    private var mediaPlayer: MediaPlayer? = null

    private fun playSound() {
        mediaPlayer = MediaPlayer.create(context, R.raw.alert)
        mediaPlayer?.setOnCompletionListener {
            it.release()
        }
        val volume = volumeManager.getVolume()
        println("Playing with $volume")
        mediaPlayer?.setVolume(volume, volume)
        mediaPlayer?.start()
    }

    override fun beepStarted() {
        playSound()
    }

    override fun beepNext() {
        playSound()
    }

    override fun beepPrevious() {
        playSound()
    }

    override fun beepFinished() {
        playSound()
    }

}
