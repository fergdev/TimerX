package com.timerx.di

import com.timerx.database.ITimerRepository
import com.timerx.database.KStoreDatabase
import com.timerx.notification.NotificationManager
import com.timerx.permissions.IPermissionsHandler
import com.timerx.permissions.PermissionManager
import com.timerx.vibration.WasmVibrationManager
import com.timerx.platform.wasmCapabilities
import com.timerx.sound.ISoundManager
import com.timerx.sound.SoundManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    includes(nonMobileModule)
    single<ITimerRepository> { KStoreDatabase() }
    single { wasmCapabilities }
    singleOf(::WasmVibrationManager)
    singleOf(::SoundManager) { bind<ISoundManager>() }
    singleOf(::NotificationManager) { createdAtStart() }
    single<IPermissionsHandler> { PermissionManager }
}
