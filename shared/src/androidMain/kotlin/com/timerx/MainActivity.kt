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
import com.timerx.database.ITimerRepository
import com.timerx.ui.App
import com.timerx.ui.navigation.NavigationProvider
import com.timerx.ui.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

const val KEY_RUN_TIMER_ID = "run_timer_id"
const val KEY_CREATE_TIMER = "create_timer"
private const val SPLASH_SCREEN_EXIT_DURATION = 500L

private const val KEEP_SPLASH_ON_DURATION = 1000L

class MainActivity : ComponentActivity() {
    private val activityModule = module {
        single<ComponentActivity> { this@MainActivity }
    }
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        splashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loadKoinModules(activityModule)
        setContent { App() }
        parseIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        parseIntent(intent)
    }

    private fun parseIntent(intent: Intent) {
        val koin = KoinPlatform.getKoin()
        val timerRepository = koin.get<ITimerRepository>()
        val navigationProvider = koin.get<NavigationProvider>()
        coroutineScope.launch {
            intent.extras?.let {
                if (it.containsKey(KEY_RUN_TIMER_ID)) {
                    val timerId = it.getLong(KEY_RUN_TIMER_ID)
                    if (timerRepository.doesTimerExist(timerId).first()) {
                        navigationProvider.navigateTo(Screen.RunScreen(timerId))
                    }
                } else if (it.containsKey(KEY_CREATE_TIMER)) {
                    navigationProvider.navigateTo(Screen.CreateScreen())
                }
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
