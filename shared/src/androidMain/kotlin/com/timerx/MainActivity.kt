package com.timerx

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.animation.doOnEnd
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arkivanov.decompose.defaultComponentContext
import com.timerx.ui.AppContent
import com.timerx.ui.navigation.DefaultRootComponent
import com.timerx.ui.navigation.RootComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

const val KEY_RUN_TIMER_ID = "run_timer_id"
const val KEY_CREATE_TIMER = "create_timer"
private const val SPLASH_SCREEN_EXIT_DURATION = 500L

private const val KEEP_SPLASH_ON_DURATION = 1000L

class MainActivity : ComponentActivity() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen()
        super.onCreate(savedInstanceState)
        // TODO see if this does anything for status bar coloring
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        val root = DefaultRootComponent(
            componentContext = defaultComponentContext()
        )
        loadKoinModules(module {
            single<ComponentActivity> { this@MainActivity }
            single<RootComponent> { root }
        })
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

    private fun splashScreen() {
        var keepScreenOn = true
        coroutineScope.launch {
            delay(KEEP_SPLASH_ON_DURATION)
            keepScreenOn = false
        }
        installSplashScreen().apply {
            setKeepOnScreenCondition { keepScreenOn }
            setOnExitAnimationListener { viewProvider ->
                ObjectAnimator.ofFloat(
                    viewProvider.view,
                    "scaleX",
                    0.5f, 0f
                ).apply {
                    interpolator = OvershootInterpolator()
                    duration = SPLASH_SCREEN_EXIT_DURATION
                    doOnEnd { viewProvider.remove() }
                    start()
                }
                ObjectAnimator.ofFloat(
                    viewProvider.view,
                    "scaleY",
                    0.5f, 0f
                ).apply {
                    interpolator = OvershootInterpolator()
                    duration = SPLASH_SCREEN_EXIT_DURATION
                    doOnEnd { viewProvider.remove() }
                    start()
                }
            }
        }
    }
}
