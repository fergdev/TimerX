package com.intervallum.platform

fun platformCapabilitiesOf(
    canSystemDynamicTheme: Boolean = false,
    canVibrate: Boolean = false,
    hasAnalytics: Boolean = false,
    hasOwnSplashScreen: Boolean = false,
    canOpenOsSettings: Boolean = false
): PlatformCapabilities =
    PlatformCapabilities(
        canSystemDynamicTheme = canSystemDynamicTheme,
        canVibrate = canVibrate,
        hasAnalytics = hasAnalytics,
        hasOwnSplashScreen = hasOwnSplashScreen,
        canOpenOsSettings = canOpenOsSettings
    )
