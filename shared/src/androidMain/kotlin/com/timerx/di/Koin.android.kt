package com.timerx.di

import com.timerx.analytics.TimerXAnalytics
import com.timerx.analytics.TimerXAnalyticsImpl
import com.timerx.contact.ContactProvider
import com.timerx.contact.ContactProviderAndroid
import com.timerx.crashlytics.CrashlyticsManager
import com.timerx.notification.NotificationManager
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.PermissionsHandler
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
    singleOf(::AndroidSoundManager) {
        createdAtStart()
        bind<SoundManager>()
    }
    singleOf(::TimerXAnalyticsImpl) { bind<TimerXAnalytics>() }
    singleOf(::AndroidVibrator) {
        createdAtStart()
        bind<VibrationManager>()
    }
    singleOf(::PermissionsHandler) { bind<IPermissionsHandler>() }
    singleOf(::NotificationManager) { createdAtStart() }
    singleOf(::CrashlyticsManager) { createdAtStart() }
    singleOf(::ContactProviderAndroid) {
        bind<ContactProvider>()
    }
}
