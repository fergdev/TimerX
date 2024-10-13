package com.timerx.ui.ads

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitViewController
import platform.UIKit.UIViewController

@Composable
actual fun GoogleAd() {
    factory?.let {
        UIKitViewController(
            factory = it,
            modifier = Modifier.size(320.dp, 50.dp),
            )
    }
}

private var factory: (() -> UIViewController)? = null

@Suppress("Unused")
fun setFactory(adFactory: () -> UIViewController) {
    factory = adFactory
}
