package com.intervallum.util

import com.intervallum.BuildConfig
import com.intervallum.BuildFlags

internal actual val BuildFlags.platform get() = Platform.Android
internal actual val BuildFlags.debuggable get() = BuildConfig.DEBUG
