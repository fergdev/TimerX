package com.timerx.vibration

import android.content.Context
import android.os.VibrationEffect
import android.os.VibratorManager
import com.timerx.settings.TimerXSettings
import org.koin.mp.KoinPlatform

class VibrationManagerImpl(private val timerXSettings: TimerXSettings) : VibrationManager {
    private val context: Context = KoinPlatform.getKoin().get()
    private val vibrator = context.getSystemService(VibratorManager::class.java).defaultVibrator

    override fun vibrate(vibration: Vibration) {
        if (timerXSettings.vibrationEnabled.not()) {
            return
        }
        val millis = when (vibration) {
            Vibration.Heavy -> 1000L
            Vibration.Medium -> 750L
            Vibration.Rigid -> 500L
            Vibration.Light -> 250L
            Vibration.Soft -> 100L
            Vibration.None -> return
        }

        vibrator.vibrate(
            VibrationEffect.createOneShot(
                millis,
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    }
}

actual fun getVibrationManager(timerXSettings: TimerXSettings): VibrationManager {
    return VibrationManagerImpl(timerXSettings)
}