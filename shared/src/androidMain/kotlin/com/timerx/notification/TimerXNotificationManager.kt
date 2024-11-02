@file:Suppress("Filename")

package com.timerx.notification

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.timerx.timermanager.TimerEvent
import com.timerx.timermanager.TimerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationManager(
    private val context: Context,
    private val timerManager: TimerManager
) {
    private val androidNotificationManager by lazy {
        ContextCompat.getSystemService(context, NotificationManager::class.java)
    }
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        coroutineScope.launch {
            timerManager.eventState.collect { timerEvent ->
                when (timerEvent) {
                    is TimerEvent.Started -> {
                        // fill out the other side of this if
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(
                                Intent(
                                    context,
                                    NotificationService::class.java
                                )
                            )
                        }
                    }

                    is TimerEvent.Finished, is TimerEvent.Destroy -> {
                        context.stopService(Intent(context, NotificationService::class.java))
                        androidNotificationManager?.cancelAll()
                    }

                    else -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            androidNotificationManager?.notify(
                                NotificationService.NOTIFICATION_ID,
                                createNotification(context, timerEvent)
                            )
                        }
                    }
                }
            }
        }
    }
}
