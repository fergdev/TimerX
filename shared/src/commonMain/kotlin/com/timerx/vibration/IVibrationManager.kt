package com.timerx.vibration

import com.timerx.settings.AlertSettingsManager
import com.timerx.settings.VibrationSetting
import com.timerx.timermanager.TimerEvent
import com.timerx.timermanager.TimerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

const val VIBRATION_DELAY: Long = 500L

abstract class VibrationManager(
    private val alertSettingsManager: AlertSettingsManager,
    private val timerManager: TimerManager
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var _isVibrationEnabled: Boolean = false
    internal val isVibrationEnabled: Boolean
        get() = _isVibrationEnabled

    init {
        coroutineScope.launch {
            combine(
                timerManager.eventState,
                alertSettingsManager.alertSettings
            ) { timerEvent, alertSettings ->
                _isVibrationEnabled =
                    (alertSettings.vibrationSetting as? VibrationSetting.CanVibrate)?.enabled ?: false
                if (_isVibrationEnabled.not()) return@combine

                when (timerEvent) {
                    is TimerEvent.Ticker -> timerEvent.vibration?.let { vibrate(it) }
                    is TimerEvent.Finished -> vibrate(timerEvent.vibration)
                    is TimerEvent.NextInterval -> vibrate(timerEvent.vibration)
                    is TimerEvent.PreviousInterval -> vibrate(timerEvent.vibration)
                    is TimerEvent.Started -> vibrate(timerEvent.vibration)
                    else -> {}
                }
            }
        }
    }

    abstract suspend fun vibrate(vibration: Vibration)
}

enum class Vibration(val displayName: String, val repeat: Int) {
    Soft("Soft", 1),
    SoftX2("Soft X2", 2),
    SoftX3("Soft X3", 3),
    Light("Light", 1),
    LightX2("Light", 2),
    LightX3("Light", 3),
    Medium("Medium", 1),
    MediumX2("Medium X2", 2),
    MediumX3("Medium X3", 3),
    Rigid("Rigid", 1),
    RigidX2("Rigid X2", 2),
    RigidX3("Rigid X3", 3),
    Heavy("Heavy", 1),
    HeavyX2("Heavy X2", 2),
    HeavyX3("Heavy X3", 3),
    None("None", 1)
}
