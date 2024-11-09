package com.timerx.util

import com.timerx.BuildFlags

internal actual val BuildFlags.platform: Platform
    get() = Platform.Desktop

internal actual val BuildFlags.debuggable: Boolean
    get() = true
