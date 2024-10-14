package com.timerx

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.pm.ShortcutManagerCompat
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.defaultComponentContext
import com.timerx.ui.AppContent
import com.timerx.ui.navigation.DefaultRootComponent
import com.timerx.ui.navigation.RootComponent
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

const val KEY_RUN_TIMER_ID = "run_timer_id"
const val KEY_CREATE_TIMER = "create_timer"

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalDecomposeApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fix for three-button nav not properly going edge-to-edge.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        // TODO: https://issuetracker.google.com/issues/298296168
        window.setFlags(FLAG_LAYOUT_NO_LIMITS, FLAG_LAYOUT_NO_LIMITS)
        val root = DefaultRootComponent(
            componentContext = defaultComponentContext()
        )
        loadKoinModules(
            module {
                single<ComponentActivity> { this@MainActivity }
                single<RootComponent> { root }
            }
        )
        setContent { AppContent(root) }
        parseIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        parseIntent(intent)
    }

    private fun parseIntent(intent: Intent) {
        val koin = KoinPlatform.getKoin()
        val rootComponent = koin.get<RootComponent>()
        intent.extras?.let {
            if (it.containsKey(KEY_RUN_TIMER_ID)) {
                val timerId = it.getLong(KEY_RUN_TIMER_ID)
                rootComponent.navigateTo(DefaultRootComponent.Config.Run(timerId))
            } else if (it.containsKey(KEY_CREATE_TIMER)) {
                ShortcutManagerCompat.reportShortcutUsed(this@MainActivity, KEY_CREATE_TIMER)
                rootComponent.navigateTo(DefaultRootComponent.Config.Create(null))
            }
        }
    }
}
