package com.timerx.di

import com.timerx.beep.ABeepManager
import com.timerx.beep.IBeepManager
import com.timerx.database.ITimerRepository
import com.timerx.database.InMemoryTimerXRepository
import com.timerx.platform.PlatformCapabilities
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val wasmCapabilities =
    PlatformCapabilities(
        isDynamicThemeSupported = false,
        vibration = false
    )
actual val platformModule = module {
    includes(nonMobileModule)
    single<ITimerRepository> { InMemoryTimerXRepository }
    single { wasmCapabilities}
    singleOf(::ABeepManager) { bind<IBeepManager>() }
}
