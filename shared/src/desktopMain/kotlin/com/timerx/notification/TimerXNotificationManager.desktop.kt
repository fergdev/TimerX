package com.timerx.notification

import com.timerx.domain.TimerEvent
import com.timerx.domain.TimerEvent.Finished
import com.timerx.domain.TimerEvent.NextInterval
import com.timerx.domain.TimerEvent.PreviousInterval
import com.timerx.domain.TimerEvent.Started
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import timerx.shared.generated.resources.Res
import timerx.shared.generated.resources.app_name
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon

object NotificationManager : ITimerXNotificationManager {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    override fun start() {
    }

    override fun stop() {
    }

    override fun updateNotification(timerEvent: TimerEvent) {
        coroutineScope.launch {
            if (timerEvent.shouldNotify()) {
                if (SystemTray.isSupported()) {
                    val tray = SystemTray.getSystemTray()
                    val image = Toolkit.getDefaultToolkit().createImage("logo.webp")
                    val trayIcon = TrayIcon(image, "Desktop Notification")
                    tray.add(trayIcon)
                    trayIcon.displayMessage(
                        getString(Res.string.app_name),
                        timerEvent.runState.intervalName,
                        TrayIcon.MessageType.INFO
                    )
                } else {
                    println("System tray is not supported")
                }
            }
        }
    }

    private fun TimerEvent.shouldNotify() =
        this is NextInterval || this is PreviousInterval ||
                this is Finished || this is Started
}
