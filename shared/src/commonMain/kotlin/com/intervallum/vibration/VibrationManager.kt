package com.intervallum.vibration

import com.intervallum.coroutines.TxDispatchers
import com.intervallum.settings.AlertSettingsManager
import com.intervallum.settings.VibrationSetting
import com.intervallum.timermanager.TimerEvent.Destroy
import com.intervallum.timermanager.TimerEvent.Finished
import com.intervallum.timermanager.TimerEvent.NextInterval
import com.intervallum.timermanager.TimerEvent.Paused
import com.intervallum.timermanager.TimerEvent.PreviousInterval
import com.intervallum.timermanager.TimerEvent.Resumed
import com.intervallum.timermanager.TimerEvent.Started
import com.intervallum.timermanager.TimerEvent.Ticker
import com.intervallum.timermanager.TimerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val VIBRATION_DELAY: Long = 100L

interface VibrationManager {
    suspend fun vibrate(vibration: Vibration)
}

abstract class AbstractVibrationManager(
    private val alertSettingsManager: AlertSettingsManager,
    private val timerManager: TimerManager,
    txDispatchers: TxDispatchers
) : VibrationManager {
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
                    is Ticker -> timerEvent.vibration?.let { vibrate(it) }
                    is Finished -> vibrate(timerEvent.vibration)
                    is NextInterval -> vibrate(timerEvent.vibration)
                    is PreviousInterval -> vibrate(timerEvent.vibration)
                    is Started -> vibrate(timerEvent.vibration)
                    is Destroy -> {}
                    is Paused -> {}
                    is Resumed -> {}
                }
            }
        }
    }
}
