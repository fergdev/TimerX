package com.timerx.platform

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

internal val androidCapabilities =
    PlatformCapabilities(
        canSystemDynamic = supportsDynamicColors,
        canVibrate = true
    )

@get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
internal val supportsDynamicColors get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
