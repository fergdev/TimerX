package com.timerx.settings

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

private const val BACKGROUND_SETTINGS = "backgroundSettings_"
private const val ALPHA = "${BACKGROUND_SETTINGS}alpha"
private const val PATTERN = "${BACKGROUND_SETTINGS}pattern"

interface BackgroundSettingsManager {
    val backgroundSettings: Flow<BackgroundSettings>
    suspend fun setBackgroundAlpha(backgroundAlpha: BackgroundAlpha)
    suspend fun setPattern(pattern: Pattern)
}

@OptIn(ExperimentalSettingsApi::class)
internal class BackgroundSettingsManagerImpl(
    private val flowSettings: FlowSettings
) : BackgroundSettingsManager {

    private val backgroundAlpha = flowSettings.getFloatOrNullFlow(ALPHA).map {
        if (it == null) BackgroundAlpha.default
        else BackgroundAlpha(it)
    }

    private val pattern = flowSettings.getIntOrNullFlow(PATTERN).map {
        if (it == null) Pattern.Bubbles
        else Pattern.entries[it]
    }

    override val backgroundSettings: Flow<BackgroundSettings> =
        combine(backgroundAlpha, pattern) { alpha, pattern ->
            BackgroundSettings(
                backgroundAlpha = alpha,
                pattern = pattern
            )
        }

    override suspend fun setBackgroundAlpha(backgroundAlpha: BackgroundAlpha) =
        flowSettings.putFloat(ALPHA, backgroundAlpha.value)

    override suspend fun setPattern(pattern: Pattern) =
        flowSettings.putInt(PATTERN, pattern.ordinal)
}

data class BackgroundSettings(
    val backgroundAlpha: BackgroundAlpha = BackgroundAlpha.default,
    val pattern: Pattern = Pattern.Bubbles,
)

enum class Pattern {
    Bubbles,
    Rectangles,
    Triangles
}
