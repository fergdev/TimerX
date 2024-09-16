package com.timerx.ui.run

import androidx.compose.ui.graphics.Color
import pro.respawn.flowmvi.api.MVIIntent
import pro.respawn.flowmvi.api.MVIState

internal sealed interface RunScreenState : MVIState {

    data object Loading : RunScreenState
    data object NoTimer : RunScreenState
    sealed interface Loaded : RunScreenState {

        val backgroundColor: Color
        val volume: Float
        val vibrationEnabled: Boolean
        val timerName: String

        sealed interface NotFinished : Loaded {
            override val backgroundColor: Color
            override val volume: Float
            override val vibrationEnabled: Boolean
            override val timerName: String
            val time: Long
            val intervalName: String
            val manualNext: Boolean
            val index: String

            data class Playing(
                override val backgroundColor: Color,
                override val volume: Float,
                override val vibrationEnabled: Boolean,
                override val timerName: String,
                override val time: Long,
                override val intervalName: String,
                override val manualNext: Boolean,
                override val index: String,
            ) : NotFinished

            data class Paused(
                override val backgroundColor: Color,
                override val volume: Float,
                override val vibrationEnabled: Boolean,
                override val timerName: String,
                override val time: Long,
                override val intervalName: String,
                override val manualNext: Boolean,
                override val index: String,
            ) : NotFinished
        }

        data class Finished(
            override val backgroundColor: Color,
            override val volume: Float,
            override val vibrationEnabled: Boolean,
            override val timerName: String,
        ) : Loaded
    }
}

internal sealed interface RunScreenIntent : MVIIntent {
    data object Play : RunScreenIntent
    data object Pause : RunScreenIntent
    data object NextInterval : RunScreenIntent
    data object PreviousInterval : RunScreenIntent
    data object OnManualNext : RunScreenIntent
    data object RestartTimer : RunScreenIntent
    data class UpdateVolume(val volume: Float) : RunScreenIntent
    data class UpdateVibrationEnabled(val enabled: Boolean) : RunScreenIntent
}
