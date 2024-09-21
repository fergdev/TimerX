package com.timerx.di

import com.timerx.analytics.ITimerXAnalytics
import com.timerx.analytics.TimerXAnalytics
import com.timerx.beep.BeepManager
import com.timerx.beep.IBeepManager
import com.timerx.capabilities.iosCapabilities
import com.timerx.notification.ITimerXNotificationManager
import com.timerx.notification.TimerXNotificationManager
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.PermissionsHandler
import com.timerx.vibration.IVibrationManager
import com.timerx.vibration.VibrationManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    includes(mobileModule)
    single { iosCapabilities }
    singleOf(::BeepManager) { bind<IBeepManager>() }
    singleOf(::TimerXAnalytics) { bind<ITimerXAnalytics>() }
    singleOf(::VibrationManager) { bind<IVibrationManager>() }
    singleOf(::PermissionsHandler) { bind<IPermissionsHandler>() }
    singleOf(::TimerXNotificationManager) { bind<ITimerXNotificationManager>() }
}
