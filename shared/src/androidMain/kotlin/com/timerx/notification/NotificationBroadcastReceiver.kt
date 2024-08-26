package com.timerx.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.timerx.domain.TimerManager
import org.koin.mp.KoinPlatform

class NotificationBroadcastReceiver : BroadcastReceiver() {
    private val timerManager = KoinPlatform.getKoin().get<TimerManager>()

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.extras?.getString(NOTIFICATION_KEY) == NOTIFICATION_PLAY) {
            timerManager.playPause()
        } else if (intent?.extras?.getString(NOTIFICATION_KEY) == NOTIFICATION_STOP) {
            timerManager.destroy()
        }
    }
}
