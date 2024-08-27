package com.timerx.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import androidx.compose.ui.graphics.toArgb
import com.timerx.domain.TimerManager
import com.timerx.domain.generateNotificationMessage
import org.koin.mp.KoinPlatform

class NotificationService : Service() {
    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }
    private val timerManager = KoinPlatform.getKoin().get<TimerManager>()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        val backgroundColor = timerManager.eventState.value.runState.backgroundColor
        val generateNotificationMessage =
            generateNotificationMessage(timerManager.eventState.value.runState)
        startForeground(
            NOTIFICATION_ID,
            createNotification(this, true, generateNotificationMessage, backgroundColor.toArgb()),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            TIMER_X,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = TIMER_X
            setSound(null, null)
        }
        notificationManager.createNotificationChannel(serviceChannel)
    }

    companion object {
        private const val TIMER_X = "TimerX"
        const val CHANNEL_ID = "TimerXServiceChannel"
        const val NOTIFICATION_ID = 1
    }
}