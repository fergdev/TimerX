package com.timerx.util

import com.timerx.BuildFlags

internal actual val BuildFlags.platform: Platform get() = Platform.Apple
internal actual val BuildFlags.debuggable: Boolean get() = true
