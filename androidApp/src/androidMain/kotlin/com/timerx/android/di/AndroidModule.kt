 package com.timerx.android.di

import com.timerx.shortcuts.ShortcutManager
import org.koin.core.module.dsl.new
import org.koin.dsl.module

val androidModule = module {
    single(createdAtStart = true) { new(::ShortcutManager) }
}