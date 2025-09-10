package com.intervallum.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView

@Composable
actual fun KeepScreenOn() {
    val localView = LocalView.current

    DisposableEffect(Unit) {
        localView.keepScreenOn = true
        onDispose {
            localView.keepScreenOn = false
        }
    }
}
