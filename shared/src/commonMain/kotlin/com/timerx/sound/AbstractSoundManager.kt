package com.timerx.sound

import com.timerx.coroutines.TDispatchers
import com.timerx.settings.AlertSettingsManager
import com.timerx.timermanager.TimerEvent
import com.timerx.timermanager.TimerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

interface SoundManager {

    suspend fun beep(beep: Beep)

    suspend fun textToSpeech(text: String)

    fun voices(): List<VoiceInformation>
}

abstract class AbstractSoundManager(
    alertSettingsManager: AlertSettingsManager,
    timerManager: TimerManager,
    tDispatchers: TDispatchers
) : SoundManager {
    abstract val isTTSSupported: Boolean
    internal var volume: Volume = Volume.default
    internal val coroutineScope = CoroutineScope(tDispatchers.default)

    init {
        coroutineScope.launch {
            alertSettingsManager.alertSettings.collect { volume = it.volume }
        }

        coroutineScope.launch {
            timerManager.eventState.collect { timerEvent ->
                when (timerEvent) {
                    is TimerEvent.Ticker -> timerEvent.beep?.let { beep(it) }
                    is TimerEvent.Finished -> makeIntervalSound(timerEvent.intervalSound)
                    is TimerEvent.NextInterval -> makeIntervalSound(timerEvent.intervalSound)
                    is TimerEvent.PreviousInterval -> makeIntervalSound(timerEvent.intervalSound)
                    is TimerEvent.Started -> makeIntervalSound(timerEvent.intervalSound)
                    is TimerEvent.Destroy -> {}
                    is TimerEvent.Paused -> {}
                    is TimerEvent.Resumed -> {}
                }
            }
        }
    }

    private suspend fun makeIntervalSound(intervalSound: IntervalSound) {
        with(intervalSound) {
            if (text != null) {
                textToSpeech(text)
            } else {
                beep(beep)
            }
        }
    }
}

@Serializable
data class VoiceInformation(
    val id: String,
    val name: String
) {
    companion object {
        val DeviceDefault = VoiceInformation(
            "", "DeviceDefault"
        )
    }
}

data class IntervalSound(
    val beep: Beep,
    val text: String? = null
)

const val BEEP_DELAY = 500L

enum class Beep(val displayName: String, val path: String, val repeat: Int) {
    Alert("Alert", "alert", 1),
    AlertX2("Alert X2", "alert", 2),
    AlertX3("Alert X3", "alert", 3),
    Alert2("Alert2", "alert2", 1),
    Alert2X2("Alert2 X2", "alert2", 2),
    Alert2X3("Alert2 X3", "alert2", 3),
    Whistle("Whistle", "whistle", 1),
    WhistleX2("Whistle X2", "whistle", 2),
    WhistleX3("Whistle X3", "whistle", 3),
    End("End", "end", 1),
    EndX2("End X2", "end", 2),
    EndX3("End X3", "end", 3)
}
