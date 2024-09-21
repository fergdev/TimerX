package com.timerx.vibration

import android.content.Context
import android.os.VibrationEffect
import android.os.VibratorManager
import com.timerx.beep.BEEP_VIBRATION_DELAY
import com.timerx.settings.ITimerXSettings
import com.timerx.vibration.Vibration.Heavy
import com.timerx.vibration.Vibration.HeavyX2
import com.timerx.vibration.Vibration.HeavyX3
import com.timerx.vibration.Vibration.Light
import com.timerx.vibration.Vibration.LightX2
import com.timerx.vibration.Vibration.LightX3
import com.timerx.vibration.Vibration.Medium
import com.timerx.vibration.Vibration.MediumX2
import com.timerx.vibration.Vibration.MediumX3
import com.timerx.vibration.Vibration.None
import com.timerx.vibration.Vibration.Rigid
import com.timerx.vibration.Vibration.RigidX2
import com.timerx.vibration.Vibration.RigidX3
import com.timerx.vibration.Vibration.Soft
import com.timerx.vibration.Vibration.SoftX2
import com.timerx.vibration.Vibration.SoftX3
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import org.koin.mp.KoinPlatform

class VibrationManager(private val timerXSettings: ITimerXSettings) : IVibrationManager {
    private val context: Context = KoinPlatform.getKoin().get()
    private val vibrator = context.getSystemService(VibratorManager::class.java).defaultVibrator

    override suspend fun vibrate(vibration: Vibration) {
        if (timerXSettings.alertSettings.first().vibrationEnabled.not()) {
            return
        }
        val millis = when (vibration) {
            Heavy, HeavyX2, HeavyX3 -> 1000L
            Medium, MediumX2, MediumX3 -> 750L
            Rigid, RigidX2, RigidX3 -> 500L
            Light, LightX2, LightX3 -> 250L
            Soft, SoftX2, SoftX3 -> 100L
            None -> return
        }

        repeat(vibration.repeat) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    millis,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
            delay(BEEP_VIBRATION_DELAY)
        }
    }
}
