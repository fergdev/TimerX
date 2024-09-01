 package com.timerx.android

import com.timerx.shortcuts.ShortcutManager
import org.koin.dsl.module

val androidModule = module {
    single { ShortcutManager(get(), get(), get()) }
}