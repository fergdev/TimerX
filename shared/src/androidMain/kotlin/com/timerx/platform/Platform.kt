package com.timerx.platform

import android.os.Build

internal val androidCapabilities =
    PlatformCapabilities(
        canSystemDynamicTheme = supportsDynamicColors,
        canVibrate = true,
        hasAnalytics = true,
        hasOwnSplashScreen = true
    )

internal val supportsDynamicColors get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
