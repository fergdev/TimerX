package com.timerx.sound

import com.timerx.settings.ITimerXSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

abstract class ISoundManager(timerXSettings: ITimerXSettings) {
    abstract val isTTSSupported: Boolean
    internal val coroutineScope = CoroutineScope(Dispatchers.Default)

    internal var volume: Float = DEFAULT_VOLUME

    init {
        coroutineScope.launch {
            timerXSettings.alertSettingsManager.alertSettings.collect { volume = it.volume }
        }
    }

    abstract suspend fun beep(beep: Beep)

    abstract suspend fun textToSpeech(text: String)

    abstract fun voices(): List<VoiceInformation>

    suspend fun makeIntervalSound(intervalSound: IntervalSound) {
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
    val text: String?,
)

const val DEFAULT_VOLUME = 0.5f
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