package com.intervallum.util

import com.intervallum.BuildFlags

internal actual val BuildFlags.platform: Platform get() = Platform.Web
internal actual val BuildFlags.debuggable: Boolean get() = true
