@file:Suppress("Filename")

package com.timerx

import androidx.compose.ui.window.ComposeUIViewController
import com.timerx.ui.navigation.RootComponent
import com.timerx.ui.AppContent

@Suppress("FunctionName")
fun MainViewController(root: RootComponent) = ComposeUIViewController {
    AppContent(root)
}
