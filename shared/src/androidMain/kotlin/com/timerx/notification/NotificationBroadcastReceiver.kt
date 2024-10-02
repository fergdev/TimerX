package com.timerx.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.timerx.timermanager.TimerManager
import org.koin.mp.KoinPlatform

class NotificationBroadcastReceiver : BroadcastReceiver() {
    private val timerManager = KoinPlatform.getKoin().get<TimerManager>()

    override fun onReceive(context: Context, intent: Intent?) {
        val string = intent?.extras?.getString(NOTIFICATION_KEY)
        when (string) {
            NOTIFICATION_PLAY_PAUSE -> timerManager.playPause()
            NOTIFICATION_STOP -> timerManager.destroy()
            NOTIFICATION_SKIP_NEXT -> timerManager.nextInterval()
            NOTIFICATION_SKIP_PREVIOUS -> timerManager.previousInterval()
        }
    }
}
