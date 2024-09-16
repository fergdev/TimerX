package com.timerx.di

import com.timerx.analytics.ITimerXAnalytics
import com.timerx.analytics.TimerXAnalytics
import com.timerx.beep.BeepManager
import com.timerx.beep.IBeepManager
import com.timerx.database.ITimerRepository
import com.timerx.database.InMemoryTimerXRepository
import com.timerx.notification.ITimerXNotificationManager
import com.timerx.notification.NotificationManager
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.PermissionHandler
import com.timerx.settings.ITimerXSettings
import com.timerx.settings.InMemoryTimerXSettings
import com.timerx.vibration.IVibrationManager
import com.timerx.vibration.VibrationManager
import org.koin.dsl.module

val nonMobileModule = module {
    single<ITimerXSettings> { InMemoryTimerXSettings }
    single<ITimerRepository> { InMemoryTimerXRepository }
    single<IBeepManager> { BeepManager }
    single<ITimerXAnalytics> { TimerXAnalytics }
    single<IVibrationManager> { VibrationManager }
    single<IPermissionsHandler> { PermissionHandler }
    single<ITimerXNotificationManager> { NotificationManager }
}