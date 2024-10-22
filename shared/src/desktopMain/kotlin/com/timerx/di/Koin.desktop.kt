package com.timerx.di

import com.timerx.contact.ContactProvider
import com.timerx.contact.ContactProviderDesktop
import com.timerx.notification.NotificationManager
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.PermissionsHandler
import com.timerx.sound.DesktopSoundManager
import com.timerx.sound.SoundManager
import com.timerx.vibration.NoopVibrationManager
import com.timerx.vibration.VibrationManager
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
