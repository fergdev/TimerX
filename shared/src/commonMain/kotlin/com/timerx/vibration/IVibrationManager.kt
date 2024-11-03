package com.timerx.vibration

import com.timerx.coroutines.TxDispatchers
import com.timerx.settings.AlertSettingsManager
import com.timerx.settings.VibrationSetting
import com.timerx.timermanager.TimerEvent
import com.timerx.timermanager.TimerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val VIBRATION_DELAY: Long = 500L

abstract class VibrationManager(
    private val alertSettingsManager: AlertSettingsManager,
    private val timerManager: TimerManager,
    txDispatchers: TxDispatchers
) {
    private val coroutineScope = CoroutineScope(txDispatchers.main)
    private var _isVibrationEnabled: Boolean = false
    internal val isVibrationEnabled: Boolean
        get() = _isVibrationEnabled

    init {
        coroutineScope.launch {
            alertSettingsManager.alertSettings.collect {
                _isVibrationEnabled =
                    (it.vibrationSetting as? VibrationSetting.CanVibrate)?.enabled
                        ?: false
            }
        }
        coroutineScope.launch {
            timerManager.eventState.collect { timerEvent ->
                if (_isVibrationEnabled.not()) return@collect

                when (timerEvent) {
                    is TimerEvent.Ticker -> timerEvent.vibration?.let { vibrate(it) }
                    is TimerEvent.Finished -> vibrate(timerEvent.vibration)
                    is TimerEvent.NextInterval -> vibrate(timerEvent.vibration)
                    is TimerEvent.PreviousInterval -> vibrate(timerEvent.vibration)
                    is TimerEvent.Started -> vibrate(timerEvent.vibration)
                    is TimerEvent.Destroy -> {}
                    is TimerEvent.Paused -> {}
                    is TimerEvent.Resumed -> {}
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
