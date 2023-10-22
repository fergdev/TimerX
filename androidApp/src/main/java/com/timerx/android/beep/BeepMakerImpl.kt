package com.timerx.android.beep

import android.media.AudioManager
import android.media.ToneGenerator

class BeepMakerImpl : BeepMaker {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    override fun beepStart() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_S_X4, 1000)
    }

    override fun beepNext() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_CALL_SIGNAL_ISDN_NORMAL, 500)
    }

    override fun beepBack() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500)
    }

    override fun beepFinished() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 1000)
    }
}