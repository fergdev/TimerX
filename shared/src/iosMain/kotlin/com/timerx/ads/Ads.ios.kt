package com.timerx.ads

import androidx.compose.runtime.Composable

@Composable
actual fun getAd() {
    // This is only implemented as a composable on android, this is empty block is required
    // because the ad serving in android is compose compatible.
    // No-op
}