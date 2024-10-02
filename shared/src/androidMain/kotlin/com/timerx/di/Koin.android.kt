package com.timerx.di

import com.timerx.analytics.ITimerXAnalytics
import com.timerx.analytics.TimerXAnalytics
import com.timerx.notification.ITimerXNotificationManager
import com.timerx.notification.NotificationManager
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.PermissionsHandler
import com.timerx.platform.androidCapabilities
import com.timerx.sound.ISoundManager
import com.timerx.sound.SoundManager
import com.timerx.vibration.IVibrationManager
import com.timerx.vibration.VibrationManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    includes(mobileModule)
    single { androidCapabilities }
    singleOf(::SoundManager) { bind<ISoundManager>() }
    singleOf(::TimerXAnalytics) { bind<ITimerXAnalytics>() }
    singleOf(::VibrationManager) { bind<IVibrationManager>() }
    singleOf(::PermissionsHandler) { bind<IPermissionsHandler>() }
    singleOf(::NotificationManager) { bind<ITimerXNotificationManager>() }
}
