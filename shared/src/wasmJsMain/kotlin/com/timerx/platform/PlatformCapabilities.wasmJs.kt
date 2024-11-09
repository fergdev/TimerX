package com.timerx.platform

import kotlinx.browser.window
import org.jetbrains.skiko.OS

private fun getNavigatorInfo(): String =
    js("navigator.userAgentData ? navigator.userAgentData.platform : navigator.platform")

private fun detectHostOs(): OS {
    val platformInfo = getNavigatorInfo().takeIf {
        it.isNotEmpty()
    } ?: window.navigator.userAgent

    return when {
        platformInfo.contains("Android", true) -> OS.Android
        platformInfo.contains("iPhone", true) -> OS.Ios
        platformInfo.contains("iOS", true) -> OS.Ios
        platformInfo.contains("iPad", true) -> OS.Ios
        platformInfo.contains("Linux", true) -> OS.Linux
        platformInfo.contains("Mac", true) -> OS.MacOS
        platformInfo.contains("Win", true) -> OS.Windows
        else -> OS.Unknown
    }
}

private val supportsVibration = arrayOf(OS.Android)

actual val platformCapabilities: PlatformCapabilities = PlatformCapabilities(
    canSystemDynamicTheme = false,
    canVibrate = detectHostOs() in supportsVibration,
    hasAnalytics = false,
    hasOwnSplashScreen = false,
    canOpenOsSettings = false,
)
