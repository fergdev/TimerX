package com.timerx.settings

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.timerx.util.mapIfNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

private const val BACKGROUND_SETTINGS = "backgroundSettings_"
private const val ALPHA = "${BACKGROUND_SETTINGS}alpha"
private const val PATTERN = "${BACKGROUND_SETTINGS}pattern"

interface BackgroundSettingsManager {
    val backgroundSettings: Flow<BackgroundSettings>
    suspend fun setAlpha(alpha: Float)
    suspend fun setPattern(pattern: Pattern)
}

@OptIn(ExperimentalSettingsApi::class)
internal class BackgroundSettingsManagerImpl(
    private val flowSettings: FlowSettings
) : BackgroundSettingsManager {

    private val backgroundAlpha = flowSettings.getFloatOrNullFlow(ALPHA).mapIfNull(0.2f)
    private val pattern = flowSettings.getIntOrNullFlow(PATTERN).map {
        if (it == null) Pattern.Bubbles
        else Pattern.entries[it]
    }

    override val backgroundSettings: Flow<BackgroundSettings> =
        combine(backgroundAlpha, pattern) { alpha, pattern ->
            BackgroundSettings(
                alpha = alpha,
                pattern = pattern
            )
        }

    override suspend fun setAlpha(alpha: Float) {
        flowSettings.putFloat(ALPHA, alpha)
    }

    override suspend fun setPattern(pattern: Pattern) {
        flowSettings.putInt(PATTERN, pattern.ordinal)
    }
}

val backgroundAlphaRange = 0.0f..0.99f

data class BackgroundSettings(
    val alpha: Float = 0.2f,
    val pattern: Pattern = Pattern.Bubbles,
)

enum class Pattern {
    Bubbles,
    Rectangles,
    Triangles
}
