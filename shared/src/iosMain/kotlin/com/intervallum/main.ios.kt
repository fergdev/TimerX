@file:Suppress("Filename")

package com.intervallum

import androidx.compose.ui.window.ComposeUIViewController
import com.intervallum.ui.AppContent
import com.intervallum.ui.navigation.RootComponent

@Suppress("FunctionName")
fun MainViewController(root: RootComponent) = ComposeUIViewController {
    AppContent(root)
}
