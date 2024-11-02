@file:Suppress("Filename")

package com.timerx.vibration

import com.timerx.coroutines.TDispatchers
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
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy
import platform.UIKit.UIImpactFeedbackStyle.UIImpactFeedbackStyleLight
import platform.UIKit.UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium
import platform.UIKit.UIImpactFeedbackStyle.UIImpactFeedbackStyleRigid
import platform.UIKit.UIImpactFeedbackStyle.UIImpactFeedbackStyleSoft

class IosVibrationManager(
    alertSettingsManager: AlertSettingsManager,
    timerManager: TimerManager,
    tDispatchers: TDispatchers
) : VibrationManager(alertSettingsManager, timerManager, tDispatchers) {

    override suspend fun vibrate(vibration: Vibration) {
        if (!isVibrationEnabled) return
        val style = when (vibration) {
            Heavy, HeavyX2, HeavyX3 -> UIImpactFeedbackStyleHeavy
            Medium, MediumX2, MediumX3 -> UIImpactFeedbackStyleMedium
            Rigid, RigidX2, RigidX3 -> UIImpactFeedbackStyleRigid
            Light, LightX2, LightX3 -> UIImpactFeedbackStyleLight
            Soft, SoftX2, SoftX3 -> UIImpactFeedbackStyleSoft
            None -> return
        }
        val generator = UIImpactFeedbackGenerator(style)
        repeat(vibration.repeat) {
            generator.impactOccurred()
            delay(VIBRATION_DELAY)
        }
    }
}
