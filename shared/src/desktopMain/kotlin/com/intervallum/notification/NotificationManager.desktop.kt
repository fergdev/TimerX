package com.intervallum.notification

import co.touchlab.kermit.Logger
import com.intervallum.timermanager.TimerEvent
import com.intervallum.timermanager.TimerEvent.Finished
import com.intervallum.timermanager.TimerEvent.NextInterval
import com.intervallum.timermanager.TimerEvent.PreviousInterval
import com.intervallum.timermanager.TimerEvent.Started
import com.intervallum.timermanager.TimerManager
import intervallum.shared.generated.resources.Res
import intervallum.shared.generated.resources.app_name
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon

class NotificationManager(timerManager: TimerManager) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        coroutineScope.launch {
            timerManager.eventState.collect { timerEvent ->
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
                        Logger.e { "System tray is not supported" }
                    }
                }
            }
        }
    }

    private fun TimerEvent.shouldNotify() =
        this is NextInterval || this is PreviousInterval ||
            this is Finished || this is Started
}
