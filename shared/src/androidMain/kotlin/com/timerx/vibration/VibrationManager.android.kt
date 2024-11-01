@file:Suppress("Filename")

package com.timerx.vibration

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import com.timerx.settings.AlertSettingsManager
import com.timerx.timermanager.TimerManager
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

class AndroidVibrator(
    alertSettingsManager: AlertSettingsManager,
    timerManager: TimerManager,
    context: Context
) : VibrationManager(alertSettingsManager, timerManager) {

    @Suppress("Deprecated")
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

    override suspend fun vibrate(vibration: Vibration) {
        if (!isVibrationEnabled) return
        val millis = when (vibration) {
            Heavy, HeavyX2, HeavyX3 -> 1000L
            Medium, MediumX2, MediumX3 -> 750L
            Rigid, RigidX2, RigidX3 -> 500L
            Light, LightX2, LightX3 -> 250L
            Soft, SoftX2, SoftX3 -> 100L
            None -> return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            repeat(vibration.repeat) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(
                        millis,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
                delay(VIBRATION_DELAY)
            }
        }
    }
}
