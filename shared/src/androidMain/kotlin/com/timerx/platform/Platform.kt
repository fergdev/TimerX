package com.timerx.platform

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

val androidCapabilities =
    PlatformCapabilities(
        dynamicColor = supportsDynamicColors,
        vibration = true
    )
@get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
internal val supportsDynamicColors get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
