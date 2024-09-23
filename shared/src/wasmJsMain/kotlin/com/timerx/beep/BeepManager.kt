package com.timerx.beep

import com.timerx.settings.ITimerXSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Audio

class ABeepManager(settings: ITimerXSettings) : IBeepManager {
    private var volume: Double = 0.0

    init {
        CoroutineScope(Dispatchers.Default).launch {
            settings.alertSettingsManager.alertSettings.collect {
                volume = it.volume.toDouble()
            }
        }
    }

    override suspend fun beep(beep: Beep) {
        Audio("https://www.soundjay.com/buttons/beep-01a.wav").apply {
            volume = this@ABeepManager.volume
        }.play()
    }
}