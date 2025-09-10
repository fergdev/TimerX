package com.intervallum.platform

expect val platformCapabilities: PlatformCapabilities

data class PlatformCapabilities(
    val canSystemDynamicTheme: Boolean,
    val canVibrate: Boolean,
    val hasAnalytics: Boolean,
    val hasOwnSplashScreen: Boolean,
    val canOpenOsSettings: Boolean
)
