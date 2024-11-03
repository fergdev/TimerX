package com.timerx.vibration

import com.timerx.coroutines.TxDispatchers
import com.timerx.settings.AlertSettingsManager
import com.timerx.settings.VibrationSetting
import com.timerx.timermanager.TimerEvent.Destroy
import com.timerx.timermanager.TimerEvent.Finished
import com.timerx.timermanager.TimerEvent.NextInterval
import com.timerx.timermanager.TimerEvent.Paused
import com.timerx.timermanager.TimerEvent.PreviousInterval
import com.timerx.timermanager.TimerEvent.Resumed
import com.timerx.timermanager.TimerEvent.Started
import com.timerx.timermanager.TimerEvent.Ticker
import com.timerx.timermanager.TimerManager
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
