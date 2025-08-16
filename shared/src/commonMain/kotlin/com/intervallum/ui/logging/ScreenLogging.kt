package com.intervallum.ui.logging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.intervallum.analytics.IntervallumAnalytics

internal val LocalIntervallumAnalytics: ProvidableCompositionLocal<IntervallumAnalytics> =
    staticCompositionLocalOf { error("IntervallumAnalytics not provided") }

@Composable
internal fun LogScreen(screenName: String) {
    val analytics = LocalIntervallumAnalytics.current
    LaunchedEffect(Unit) { analytics.logScreen(screenName) }
}
