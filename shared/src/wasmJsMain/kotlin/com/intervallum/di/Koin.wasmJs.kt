package com.intervallum.di

import com.intervallum.contact.ContactProvider
import com.intervallum.contact.ContactProviderWasm
import com.intervallum.database.ITimerRepository
import com.intervallum.database.KStoreDatabase
import com.intervallum.notification.NotificationManager
import com.intervallum.permissions.IPermissionsHandler
import com.intervallum.permissions.PermissionsHandler
import com.intervallum.sound.SoundManager
import com.intervallum.sound.WasmSoundManager
import com.intervallum.vibration.VibrationManager
import com.intervallum.vibration.WasmVibrationManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    includes(nonMobileModule)
    single<ITimerRepository> { KStoreDatabase() }
    singleOf(::WasmVibrationManager) {
        createdAtStart()
        bind<VibrationManager>()
    }
    singleOf(::WasmSoundManager) {
        createdAtStart()
        bind<SoundManager>()
    }
    singleOf(::NotificationManager) { createdAtStart() }
    singleOf(::ContactProviderWasm) { bind<ContactProvider>() }
    singleOf(::PermissionsHandler) { bind<IPermissionsHandler>() }
}
