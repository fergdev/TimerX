package com.timerx

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.timerx.ui.App
import com.timerx.ui.NavigationProvider
import com.timerx.ui.Screen
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

const val KEY_TIMER_ID = "timer_id"
const val KEY_CREATE = "create"

class MainActivity : ComponentActivity() {
    private val module = module {
        single<ComponentActivity> { this@MainActivity }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }
        loadKoinModules(module)
        parseIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        parseIntent(intent)
    }

    private fun parseIntent(intent: Intent) {
        intent.extras?.let {
            if (it.containsKey(KEY_TIMER_ID)) {
                KoinPlatform.getKoin().get<NavigationProvider>()
                    .navigationTo(Screen.RunScreen(it.getLong(KEY_TIMER_ID)))
            } else if(it.containsKey(KEY_CREATE)){
                KoinPlatform.getKoin().get<NavigationProvider>()
                    .navigationTo(Screen.CreateScreen())
            }
        }
    }

    override fun onStop() {
        super.onStop()
        unloadKoinModules(module)
    }
}