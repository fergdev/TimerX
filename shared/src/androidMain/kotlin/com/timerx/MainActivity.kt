package com.timerx

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.timerx.domain.TimerManager
import com.timerx.notification.NOTIFICATION_KEY
import com.timerx.notification.NOTIFICATION_PLAY
import com.timerx.notification.NOTIFICATION_STOP
import com.timerx.ui.App
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

class MainActivity : ComponentActivity() {
    private val module = module {
        single<ComponentActivity> { this@MainActivity }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }
        loadKoinModules(module)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.extras?.getString(NOTIFICATION_KEY) == NOTIFICATION_PLAY) {
            KoinPlatform.getKoin().get<TimerManager>().playPause()
        } else if (intent.extras?.getString(NOTIFICATION_KEY) == NOTIFICATION_STOP) {
            KoinPlatform.getKoin().get<TimerManager>().destroy()
        }
    }

    override fun onStop() {
        super.onStop()
        unloadKoinModules(module)
    }
}