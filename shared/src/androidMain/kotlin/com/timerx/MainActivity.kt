package com.timerx

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.timerx.shortcuts.ShortcutManager
import com.timerx.ui.App
import com.timerx.ui.navigation.NavigationProvider
import com.timerx.ui.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

const val KEY_RUN_TIMER_ID = "run_timer_id"
const val KEY_CREATE_TIMER = "create_timer"
private const val splashScreenExitDuration = 500L

class MainActivity : ComponentActivity() {
    private val activityModule = module {
        single<ComponentActivity> { this@MainActivity }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashScreen()
        enableEdgeToEdge()
        setContent { App() }
        loadKoinModules(activityModule)
        KoinPlatform.getKoin().get<ShortcutManager>()
        parseIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        parseIntent(intent)
    }

    private fun parseIntent(intent: Intent) {
        intent.extras?.let {
            if (it.containsKey(KEY_RUN_TIMER_ID)) {
                KoinPlatform.getKoin().get<NavigationProvider>()
                    .navigateTo(Screen.RunScreen(it.getLong(KEY_RUN_TIMER_ID)))
            } else if (it.containsKey(KEY_CREATE_TIMER)) {
                KoinPlatform.getKoin().get<NavigationProvider>()
                    .navigateTo(Screen.CreateScreen())
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unloadKoinModules(activityModule)
    }

    private fun splashScreen() {
        var keepScreenOn = true
        CoroutineScope(Dispatchers.IO).launch {
            delay(1000)
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
                    duration = splashScreenExitDuration
                    doOnEnd { viewProvider.remove() }
                    start()
                }
                ObjectAnimator.ofFloat(
                    viewProvider.view,
                    "scaleY",
                    0.5f, 0f
                ).apply {
                    interpolator = OvershootInterpolator()
                    duration = splashScreenExitDuration
                    doOnEnd { viewProvider.remove() }
                    start()
                }
            }
        }
    }
}