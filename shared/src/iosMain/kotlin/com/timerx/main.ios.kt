@file:Suppress("Filename")

package com.timerx

import androidx.compose.ui.window.ComposeUIViewController
import com.timerx.ui.AppContent
import com.timerx.ui.navigation.RootComponent

@Suppress("FunctionName")
fun MainViewController(root: RootComponent) = ComposeUIViewController {
    AppContent(root)
}
