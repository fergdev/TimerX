package com.timerx.util

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast

@get:ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
val supportsDynamicColors get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
