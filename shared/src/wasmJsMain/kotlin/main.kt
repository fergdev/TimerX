@file:Suppress("MissingPackageDeclaration", "Filename")

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.timerx.di.startKoin
import com.timerx.ui.App
import kotlinx.browser.document
import org.koin.compose.KoinContext

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin()

    ComposeViewport(document.body!!) { KoinContext {  App() } }
}
