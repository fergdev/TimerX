@file:Suppress("MissingPackageDeclaration", "Filename")

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.stack.webhistory.DefaultWebHistoryController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import com.timerx.di.startKoin
import com.timerx.ui.AppContent
import com.timerx.ui.navigation.DefaultRootComponent
import kotlinx.browser.document
import kotlinx.browser.window
import org.koin.compose.KoinContext
import org.w3c.dom.Document

@OptIn(ExperimentalComposeUiApi::class, ExperimentalDecomposeApi::class)
fun main() {
    startKoin()
    val registry = LifecycleRegistry()
    val root = DefaultRootComponent(
        webHistoryController = DefaultWebHistoryController(),
        componentContext = DefaultComponentContext(lifecycle = registry)
    )
    registry.attachToDocument()
    window.onbeforeunload = { null }
    ComposeViewport(document.body!!) { KoinContext { AppContent(root) } }
}

private fun LifecycleRegistry.attachToDocument() {
    fun onVisibilityChanged() {
        if (visibilityState(document) == "visible") {
            resume()
        } else {
            stop()
        }
    }

    onVisibilityChanged()

    document.addEventListener(type = "visibilitychange", callback = { onVisibilityChanged() })
}

// Workaround for Document#visibilityState not available in Wasm
@JsFun("(document) => document.visibilityState")
private external fun visibilityState(document: Document): String
