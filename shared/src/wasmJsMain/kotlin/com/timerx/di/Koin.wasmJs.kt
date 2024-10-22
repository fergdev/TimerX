package com.timerx.di

import com.timerx.contact.ContactProvider
import com.timerx.contact.ContactProviderWasm
import com.timerx.database.ITimerRepository
import com.timerx.database.KStoreDatabase
import com.timerx.notification.NotificationManager
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.PermissionManager
import com.timerx.sound.WasmSoundManager
import com.timerx.vibration.WasmVibrationManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    includes(nonMobileModule)
    single<ITimerRepository> { KStoreDatabase() }
    singleOf(::WasmVibrationManager) { createdAtStart() }
    singleOf(::WasmSoundManager) { createdAtStart() }
    singleOf(::NotificationManager) { createdAtStart() }
    singleOf(::ContactProviderWasm) { bind<ContactProvider>() }
    singleOf(::PermissionManager) { bind<IPermissionsHandler>() }
}
