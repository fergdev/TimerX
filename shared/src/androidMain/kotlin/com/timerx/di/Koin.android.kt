package com.timerx.di

import com.timerx.analytics.ITimerXAnalytics
import com.timerx.analytics.TimerXAnalytics
import com.timerx.notification.NotificationManager
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.PermissionsHandler
import com.timerx.platform.androidCapabilities
import com.timerx.sound.AndroidSoundManager
import com.timerx.sound.SoundManager
import com.timerx.vibration.AndroidVibrator
import com.timerx.vibration.VibrationManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    includes(mobileModule)
    single { androidCapabilities }
    singleOf(::AndroidSoundManager) {
        createdAtStart()
        bind<SoundManager>()
    }
    singleOf(::TimerXAnalytics) { bind<ITimerXAnalytics>() }
    singleOf(::AndroidVibrator) {
        createdAtStart()
        bind<VibrationManager>()
    }
    singleOf(::PermissionsHandler) { bind<IPermissionsHandler>() }
    singleOf(::NotificationManager) { createdAtStart() }
}
