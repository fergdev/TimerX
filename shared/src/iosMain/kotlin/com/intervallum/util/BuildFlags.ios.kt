package com.intervallum.util

import com.intervallum.BuildFlags

internal actual val BuildFlags.platform: Platform get() = Platform.Apple
internal actual val BuildFlags.debuggable: Boolean get() = true
