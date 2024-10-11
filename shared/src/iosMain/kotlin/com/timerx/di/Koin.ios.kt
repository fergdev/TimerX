package com.timerx.di

import com.timerx.analytics.ITimerXAnalytics
import com.timerx.analytics.TimerXAnalytics
import com.timerx.background.BackgroundManager
import com.timerx.capabilities.iosCapabilities
import com.timerx.notification.NotificationManager
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.PermissionsHandler
import com.timerx.sound.ISoundManager
import com.timerx.sound.SoundManager
import com.timerx.vibration.IosVibrationManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    includes(mobileModule)
    single { iosCapabilities }
    singleOf(::SoundManager) { bind<ISoundManager>() }
    singleOf(::TimerXAnalytics) { bind<ITimerXAnalytics>() }
    singleOf(::IosVibrationManager)
    singleOf(::PermissionsHandler) { bind<IPermissionsHandler>() }
    singleOf(::BackgroundManager) { createdAtStart() }
    singleOf(::NotificationManager) { createdAtStart() }
}
