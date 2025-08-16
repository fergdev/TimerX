package com.intervallum.di

import com.intervallum.analytics.IntervallumAnalytics
import com.intervallum.analytics.IntervallumAnalyticsImpl
import com.intervallum.background.BackgroundManager
import com.intervallum.contact.ContactProvider
import com.intervallum.contact.ContactProviderIos
import com.intervallum.crashlytics.CrashlyticsManager
import com.intervallum.notification.NotificationManager
import com.intervallum.permissions.IPermissionsHandler
import com.intervallum.permissions.PermissionsHandler
import com.intervallum.sound.IosSoundManager
import com.intervallum.sound.SoundManager
import com.intervallum.vibration.IosVibrationManager
import com.intervallum.vibration.VibrationManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    includes(mobileModule)
    singleOf(::IosSoundManager) {
        createdAtStart()
        bind<SoundManager>()
        bind<IosSoundManager>()
    }
    singleOf(::IntervallumAnalyticsImpl) { bind<IntervallumAnalytics>() }
    singleOf(::IosVibrationManager) {
        createdAtStart()
        bind<VibrationManager>()
    }
    singleOf(::PermissionsHandler) { bind<IPermissionsHandler>() }
    singleOf(::BackgroundManager) { createdAtStart() }
    singleOf(::NotificationManager) { createdAtStart() }
    singleOf(::CrashlyticsManager) { createdAtStart() }
    singleOf(::ContactProviderIos) {
        bind<ContactProvider>()
    }
}
