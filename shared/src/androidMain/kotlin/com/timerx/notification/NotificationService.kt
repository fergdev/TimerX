package com.timerx.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.timerx.timermanager.TimerManager
import org.koin.mp.KoinPlatform

@RequiresApi(Build.VERSION_CODES.Q)
class NotificationService : Service() {
    private val notificationManager by lazy {
        getSystemService(NotificationManager::class.java)
    }
    private val timerManager = KoinPlatform.getKoin().get<TimerManager>()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(
            NOTIFICATION_ID,
            createNotification(this, timerManager.eventState.value),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            TIMER_X,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = TIMER_X
            setSound(null, null)
            setShowBadge(false)
            enableVibration(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            importance = NotificationManager.IMPORTANCE_HIGH
        }
        notificationManager.createNotificationChannel(serviceChannel)
    }

    companion object {
        private const val TIMER_X = "TimerX"
        const val CHANNEL_ID = "TimerXServiceChannel"
        const val NOTIFICATION_ID = 1
    }
}
