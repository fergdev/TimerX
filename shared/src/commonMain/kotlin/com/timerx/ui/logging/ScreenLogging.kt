package com.timerx.ui.logging

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.timerx.analytics.TimerXAnalytics

internal val LocalTimerXAnalytics: ProvidableCompositionLocal<TimerXAnalytics> =
    staticCompositionLocalOf { error("TimerXAnalytics not provided") }

@Composable
internal fun LogScreen(screenName: String) {
    val analytics = LocalTimerXAnalytics.current
    LaunchedEffect(Unit) { analytics.logScreen(screenName) }
}
