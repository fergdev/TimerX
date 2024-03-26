package com.timerx.beep

import android.media.AudioManager
import android.media.ToneGenerator

actual fun getBeepMaker(): BeepMaker = BeepMakerImpl()

class BeepMakerImpl : BeepMaker {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, VOLUME)

    override fun beepStart() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_S_X4, THOUSAND_MILLIS)
    }

    override fun beepNext() {
        toneGenerator.startTone(
            ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_NORMAL,
            FIVE_HUNDRED_MILLIS
        )
    }

    override fun beepBack() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, FIVE_HUNDRED_MILLIS)
    }

    override fun beepFinished() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, THOUSAND_MILLIS)
    }

    companion object {
        private const val VOLUME = 100
        private const val THOUSAND_MILLIS = 1000
        private const val FIVE_HUNDRED_MILLIS = 500
    }
}
