package com.intervallum.di

import com.intervallum.analytics.IntervallumAnalytics
import com.intervallum.analytics.IntervallumAnalyticsImpl
import com.intervallum.contact.ContactProvider
import com.intervallum.contact.ContactProviderAndroid
import com.intervallum.crashlytics.CrashlyticsManager
import com.intervallum.notification.NotificationManager
import com.intervallum.permissions.IPermissionsHandler
import com.intervallum.permissions.PermissionsHandler
import com.intervallum.sound.AndroidSoundManager
import com.intervallum.sound.SoundManager
import com.intervallum.vibration.AndroidVibrator
import com.intervallum.vibration.VibrationManager
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
    singleOf(::IntervallumAnalyticsImpl) { bind<IntervallumAnalytics>() }
    singleOf(::AndroidVibrator) {
        createdAtStart()
        bind<VibrationManager>()
    }
    singleOf(::PermissionsHandler) { bind<IPermissionsHandler>() }
    singleOf(::NotificationManager) { createdAtStart() }
    singleOf(::CrashlyticsManager) { createdAtStart() }
    singleOf(::ContactProviderAndroid) { bind<ContactProvider>() }
}
