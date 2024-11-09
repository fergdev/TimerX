package com.timerx.util

import com.timerx.BuildFlags

internal actual val BuildFlags.platform: Platform get() = Platform.Web
internal actual val BuildFlags.debuggable: Boolean get() = true
