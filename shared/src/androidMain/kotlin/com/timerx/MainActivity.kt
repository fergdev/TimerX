package com.timerx

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.timerx.shortcuts.ShortcutManager
import com.timerx.ui.App
import com.timerx.ui.navigation.NavigationProvider
import com.timerx.ui.navigation.Screen
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

const val KEY_RUN_TIMER_ID = "run_timer_id"
const val KEY_CREATE_TIMER = "create_timer"

class MainActivity : ComponentActivity() {
    private val activityModule = module {
        single<ComponentActivity> { this@MainActivity }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}