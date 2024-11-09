package com.timerx.util

import com.timerx.BuildConfig
import com.timerx.BuildFlags

internal actual val BuildFlags.platform get() = Platform.Android
internal actual val BuildFlags.debuggable get() = BuildConfig.DEBUG
