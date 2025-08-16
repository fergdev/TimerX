@file:Suppress("Filename")

package com.intervallum.vibration

import com.intervallum.coroutines.TxDispatchers
import com.intervallum.settings.AlertSettingsManager
import com.intervallum.timermanager.TimerManager
import com.intervallum.vibration.Vibration.Heavy
import com.intervallum.vibration.Vibration.HeavyX2
import com.intervallum.vibration.Vibration.HeavyX3
import com.intervallum.vibration.Vibration.Light
import com.intervallum.vibration.Vibration.LightX2
import com.intervallum.vibration.Vibration.LightX3
import com.intervallum.vibration.Vibration.Medium
import com.intervallum.vibration.Vibration.MediumX2
import com.intervallum.vibration.Vibration.MediumX3
import com.intervallum.vibration.Vibration.None
import com.intervallum.vibration.Vibration.Rigid
import com.intervallum.vibration.Vibration.RigidX2
import com.intervallum.vibration.Vibration.RigidX3
import com.intervallum.vibration.Vibration.Soft
import com.intervallum.vibration.Vibration.SoftX2
import com.intervallum.vibration.Vibration.SoftX3
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
    txDispatchers: TxDispatchers
) : AbstractVibrationManager(alertSettingsManager, timerManager, txDispatchers) {

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
