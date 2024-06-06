package com.timerx.platform

import platform.UIKit.UIDevice

actual class Platform actual constructor() {
    actual val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}