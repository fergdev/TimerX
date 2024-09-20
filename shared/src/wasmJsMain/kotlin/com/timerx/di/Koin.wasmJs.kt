package com.timerx.di

import com.timerx.beep.ABeepManager
import com.timerx.beep.IBeepManager
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    includes(nonMobileModule)
    singleOf(::ABeepManager) { bind<IBeepManager>() }
}
