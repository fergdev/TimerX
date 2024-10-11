package com.timerx.di

import com.timerx.capabilities.desktopCapabilities
import com.timerx.notification.NotificationManager
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.PermissionsHandler
import com.timerx.sound.ISoundManager
import com.timerx.sound.SoundManager
import com.timerx.vibration.NoopVibrationManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    includes(nonMobileModule)
    includes(mobileModule)
    single { desktopCapabilities }
    singleOf(::SoundManager) {
        bind<ISoundManager>()
        // Give TTS time to init
        createdAtStart()
    }
    singleOf(::NoopVibrationManager)
    singleOf(::PermissionsHandler) { bind<IPermissionsHandler>() }
    singleOf(::NotificationManager) { createdAtStart() }
}
