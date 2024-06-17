package com.timerx.vibration

import android.content.Context
import android.os.VibrationEffect
import android.os.VibratorManager
import com.timerx.beepVibrationDelay
import com.timerx.settings.TimerXSettings
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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.mp.KoinPlatform

class VibrationManager(private val timerXSettings: TimerXSettings) : IVibrationManager {
    private val context: Context = KoinPlatform.getKoin().get()
    private val vibrator = context.getSystemService(VibratorManager::class.java).defaultVibrator

    @OptIn(DelicateCoroutinesApi::class)
    override fun vibrate(vibration: Vibration) {
        if (timerXSettings.vibrationEnabled.not()) {
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

        GlobalScope.launch {
            repeat(vibration.repeat) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        millis,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
                delay(beepVibrationDelay)
            }
        }
    }
}

actual fun getVibrationManager(timerXSettings: TimerXSettings): IVibrationManager {
    return VibrationManager(timerXSettings)
}