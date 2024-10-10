package com.timerx.platform

import android.os.Build

internal val androidCapabilities =
    PlatformCapabilities(
        canSystemDynamic = supportsDynamicColors,
        canVibrate = true
    )

internal val supportsDynamicColors get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
