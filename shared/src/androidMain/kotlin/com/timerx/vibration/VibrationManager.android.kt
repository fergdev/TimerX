@file:Suppress("Filename")

package com.timerx.vibration

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.content.ContextCompat
import com.timerx.coroutines.TxDispatchers
import com.timerx.settings.AlertSettingsManager
import com.timerx.timermanager.TimerManager
import kotlinx.coroutines.delay

class AndroidVibrator(
    alertSettingsManager: AlertSettingsManager,
    timerManager: TimerManager,
    context: Context,
    txDispatchers: TxDispatchers
) : AbstractVibrationManager(alertSettingsManager, timerManager, txDispatchers) {

    private val vibrator = ContextCompat.getSystemService(context, Vibrator::class.java)

    override suspend fun vibrate(vibration: Vibration) {
        if (!isVibrationEnabled) return
        val millis = vibration.toMillis()
        repeat(vibration.repeat) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(
                        millis,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                @Suppress("Deprecated")
                vibrator?.vibrate(millis)
            }
            delay(VIBRATION_DELAY + millis)
        }
    }
}
