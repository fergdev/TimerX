package com.timerx.di

import com.timerx.analytics.ITimerXAnalytics
import com.timerx.analytics.TimerXAnalytics
import com.timerx.notification.ITimerXNotificationManager
import com.timerx.notification.NotificationManager
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.PermissionHandler
import com.timerx.vibration.IVibrationManager
import com.timerx.vibration.NoopVibrationManager
import org.koin.dsl.module

val nonMobileModule = module {
    single<ITimerXAnalytics> { TimerXAnalytics }
    single<IVibrationManager> { NoopVibrationManager }
    single<IPermissionsHandler> { PermissionHandler }
    single<ITimerXNotificationManager> { NotificationManager }
}
