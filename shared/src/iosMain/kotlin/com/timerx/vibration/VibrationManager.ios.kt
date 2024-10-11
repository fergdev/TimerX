package com.timerx.vibration

import com.timerx.settings.ITimerXSettings
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
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

class IosVibrationManager(
    timerXSettings: ITimerXSettings,
    timerManager: TimerManager,
) : VibrationManager(timerXSettings, timerManager) {

    override suspend fun vibrate(vibration: Vibration) {
        if (!isVibrationEnabled) return
        val style = when (vibration) {
            Heavy, HeavyX2, HeavyX3 -> UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy
            Medium, MediumX2, MediumX3 -> UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium
            Rigid, RigidX2, RigidX3 -> UIImpactFeedbackStyle.UIImpactFeedbackStyleRigid
            Light, LightX2, LightX3 -> UIImpactFeedbackStyle.UIImpactFeedbackStyleLight
            Soft, SoftX2, SoftX3 -> UIImpactFeedbackStyle.UIImpactFeedbackStyleSoft
            None -> return
        }
        val generator = UIImpactFeedbackGenerator(style)
        repeat(vibration.repeat) {
            generator.impactOccurred()
            delay(VIBRATION_DELAY)
        }
    }
}
