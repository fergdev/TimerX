package com.timerx.vibration

import com.timerx.settings.TimerXSettings
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

class VibrationManagerImpl(private val timerXSettings: TimerXSettings) : VibrationManager {
    override fun vibrate(vibration: Vibration) {
        if (timerXSettings.vibrationEnabled.not()) {
            return
        }
        val style = when (vibration) {
            Vibration.Heavy -> UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy
            Vibration.Medium -> UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium
            Vibration.Rigid -> UIImpactFeedbackStyle.UIImpactFeedbackStyleRigid
            Vibration.Light -> UIImpactFeedbackStyle.UIImpactFeedbackStyleLight
            Vibration.Soft -> UIImpactFeedbackStyle.UIImpactFeedbackStyleSoft
            Vibration.None -> return
        }
        val generator = UIImpactFeedbackGenerator(style)
        generator.impactOccurred()
    }
}

actual fun getVibrationManager(timerXSettings: TimerXSettings): VibrationManager =
    VibrationManagerImpl(timerXSettings)