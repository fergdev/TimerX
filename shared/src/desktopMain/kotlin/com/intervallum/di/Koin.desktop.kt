package com.intervallum.di

import com.intervallum.contact.ContactProvider
import com.intervallum.contact.ContactProviderDesktop
import com.intervallum.notification.NotificationManager
import com.intervallum.permissions.IPermissionsHandler
import com.intervallum.permissions.PermissionsHandler
import com.intervallum.sound.DesktopSoundManager
import com.intervallum.sound.SoundManager
import com.intervallum.vibration.NoopVibrationManager
import com.intervallum.vibration.VibrationManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    includes(nonMobileModule)
    includes(mobileModule)
    singleOf(::DesktopSoundManager) {
        createdAtStart()
        bind<SoundManager>()
    }
    singleOf(::NoopVibrationManager) {
        bind<VibrationManager>()
    }
    singleOf(::PermissionsHandler) { bind<IPermissionsHandler>() }
    singleOf(::NotificationManager) { createdAtStart() }
    singleOf(::ContactProviderDesktop) { bind<ContactProvider>() }
}
